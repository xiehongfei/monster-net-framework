package org.monster.framework.tes.common.utils.ftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.monster.framework.tes.common.annotation.MetaData;

import java.io.*;

/**
 * Project :       Monster-frameWork
 * Author:         XIE-HONGFEI
 * Company:        hongfei tld.
 * Created Date:   2017/2/25 0025
 * Copyright @ 2017 Company hongfei tld. – Confidential and Proprietary
 * <p>
 * History:
 * ------------------------------------------------------------------------------
 *        Date       |     Author     |   Change Description
 *  2017/2/25 0025   |     谢洪飞      |   初版做成
 */

public class FtpClients {

    public FTPClient ftpClient = new FTPClient();

    private String clientEncoding = "UTF-8";

    public FtpClients(){
        //TODO 设置将过程中用到的命令输出到控制台
        // this.ftpClient.addProtocolCommandListener(new
        // PrintCommandListener(new PrintWriter(System.out)));
    }

    public FtpClients( String clientEncoding){
        //TODO 设置将过程中用到的命令输出到控制台
        // this.ftpClient.addProtocolCommandListener(new
        // PrintCommandListener(new PrintWriter(System.out)));
        if(StringUtils.isNotBlank(clientEncoding)){
            this.clientEncoding = clientEncoding;
        }
    }


    public boolean connect( final String hostname , final int port , final String userName , final String password ) throws Exception {

        try {
            ftpClient.connect(hostname , port);
        } catch (IOException e) {
            throw
                    new Exception("FTP Client 连接异常,请检查主机端口！");
        }

        ftpClient.setControlEncoding(clientEncoding);

        if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){

            if( ftpClient.login( userName , password ) )
                return true;
            else
                throw  new Exception("FTP Client 登陆异常，请检查账号密码！");
        }
        else
         throw new Exception("登陆异常！");

    }


    /**
     * 获取服务器上文件（名称）集合
     *
     * @param filedir  获取文件的指定目录
     * @return
     * @throws IOException
     */
    public String[] getFileList( String filedir ) throws IOException {

        //通知ftpserver开启一个端口来传输数据，
        // 防止linux或其它操作系统服务器上面，由于安全限制，某些端口未能开启，导致出现阻塞
        ftpClient.enterLocalPassiveMode();

        FTPFile[] files = ftpClient.listFiles(filedir);
        String [] filearr = null;

        if( null != files ){
            filearr = new String[files.length];
            for( int i = 0  ; i < files.length ; i ++ ){
                filearr[i] = files[i].getName();
            }
        }

        return filearr;
    }

    public DownloadStatusEnum download( String remote , String local ) throws IOException {


        // 设置被动模式
        ftpClient.enterLocalPassiveMode();

        // 设置以二进制方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        DownloadStatusEnum result;

        // 检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"), "iso-8859-1"));
        if (files.length != 1)
        {
            // System.out.println("远程文件不存在");
            //logger.info("远程文件不存在");
            return DownloadStatusEnum.REMOTE_FILE_NOEXIST;
        }

        long lRemoteSize = files[0].getSize();
        File f = new File(local);
        // 本地存在文件，进行断点下载
        if (f.exists())
        {
            long localSize = f.length();
            // 判断本地文件大小是否大于远程文件大小
            if (localSize >= lRemoteSize)
            {
                //logger.info("本地文件大于远程文件，下载中止");
                return DownloadStatusEnum.LOCAL_BIGGER_REMOTE;
            }

            // 进行断点续传，并记录状态
            FileOutputStream out = new FileOutputStream(f, true);
            // 找出本地已经接收了多少
            ftpClient.setRestartOffset(localSize);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
            try
            {
                byte[] bytes = new byte[1024];
                // 总的进度
                long step = lRemoteSize / 100;
                long process = localSize / step;
                int c;
                while ((c = in.read(bytes)) != -1)
                {
                    out.write(bytes, 0, c);
                    localSize += c;
                    long nowProcess = localSize / step;
                    if (nowProcess > process)
                    {
                        process = nowProcess;
                        if (process % 10 == 0)
                            //logger.info("下载进度：" + process);
                            System.out.println("下载进度:"+process);
                        // TODO 更新文件下载进度,值存放在process变量中
                    }
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }

            // 确认是否全部下载完毕
            boolean isDo = ftpClient.completePendingCommand();
            if (isDo)
            {
                result = DownloadStatusEnum.DOWNLOAD_FROM_BREAK_SUCCESS;
            }
            else
            {
                result = DownloadStatusEnum.DOWNLOAD_FROM_BREAK_FAILED;
            }
        }
        else
        {
            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
            try
            {
                byte[] bytes = new byte[1024];
                long step = lRemoteSize / 100;
                long process = 0;
                long localSize = 0L;
                int c;
                while ((c = in.read(bytes)) != -1)
                {
                    out.write(bytes, 0, c);
                    localSize += c;
                    long nowProcess = localSize / step;
                    if (nowProcess > process)
                    {
                        process = nowProcess;
                        if (process % 10 == 0)
                            System.out.println("下载进度:"+process);
                           // logger.info("下载进度：" + process);
                        // TODO 更新文件下载进度,值存放在process变量中
                    }
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
            boolean upNewStatus = ftpClient.completePendingCommand();
            if (upNewStatus)
            {
                result = DownloadStatusEnum.DOWNLOAD_NEW_SUCCESS;
            }
            else
            {
                result = DownloadStatusEnum.DOWNLOAD_FROM_BREAK_FAILED;
            }
        }
        return result;
    }



    /**
     * 上传文件到FTP服务器，支持断点续传
     *
     * @param local 本地文件名称，绝对路径
     * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
     * @return 上传结果
     * @throws IOException
     */
    public UploadStatusEnum upload(String local, String remote)
            throws IOException
    {
        // 设置PassiveMode传输
        ftpClient.enterLocalPassiveMode();
        // 设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding("GBK");
        UploadStatusEnum result;
        // 对远程目录的处理
        String remoteFileName = remote;
        if (remote.contains("/"))
        {
            remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
            // 创建服务器远程目录结构，创建失败直接返回
            if (CreateDirecroty(remote, ftpClient) == UploadStatusEnum.CREATE_DIR_FAILED)
            {
                return UploadStatusEnum.CREATE_DIR_SUCCESS;
            }
        }

        // 检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"), "iso-8859-1"));
        if (files.length == 1)
        {
            long remoteSize = files[0].getSize();
            File f = new File(local);
            long localSize = f.length();
            if (remoteSize == localSize)
            {
                return UploadStatusEnum.FILE_EXITS;
            }
            else if (remoteSize > localSize)
            {
                return UploadStatusEnum.REMOTE_BIGGER_LOCAL;
            }

            // 尝试移动文件内读取指针,实现断点续传
            result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

            // 如果断点续传没有成功，则删除服务器上文件，重新上传
            if (result == UploadStatusEnum.UPLOAD_FROM_BREAK_FAILED)
            {
                if (!ftpClient.deleteFile(remoteFileName))
                {
                    return UploadStatusEnum.DELETE_REMOTE_FAILED;
                }
                result = uploadFile(remoteFileName, f, ftpClient, 0);
            }
        }
        else
        {
            result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
        }
        return result;
    }

    /**
     * 断开与远程服务器的连接
     *
     * @throws IOException
     */
    public void disconnect()
            throws IOException
    {
        if (ftpClient.isConnected())
        {
            ftpClient.disconnect();
        }
    }

    /**
     * 递归创建远程服务器目录
     *
     * @param remote 远程服务器文件绝对路径
     * @param ftpClient FTPClient对象
     * @return 目录创建是否成功
     * @throws IOException
     */
    public UploadStatusEnum CreateDirecroty(String remote, FTPClient ftpClient)
            throws IOException
    {
        UploadStatusEnum status = UploadStatusEnum.CREATE_DIR_SUCCESS;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        if (!directory.equalsIgnoreCase("/")
                && !ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"), "iso-8859-1")))
        {
            // 如果远程目录不存在，则递归创建远程服务器目录
            int start = 0;
            int end = 0;
            if (directory.startsWith("/"))
            {
                start = 1;
            }
            else
            {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true)
            {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                if (!ftpClient.changeWorkingDirectory(subDirectory))
                {
                    if (ftpClient.makeDirectory(subDirectory))
                    {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    }
                    else
                    {
                        System.out.println("创建目录失败");
                        return UploadStatusEnum.CREATE_DIR_FAILED;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/", start);

                // 检查所有目录是否创建完毕
                if (end <= start)
                {
                    break;
                }
            }
        }
        return status;
    }

    /**
     * 上传文件到服务器,新上传和断点续传
     *
     * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变
     * @param localFile 本地文件File句柄，绝对路径
     * @param //processStep 需要显示的处理进度步进值
     * @param ftpClient FTPClient引用
     * @return
     * @throws IOException
     */
    public UploadStatusEnum uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize)
            throws IOException
    {
        UploadStatusEnum status;
        // 显示进度的上传
        long step = localFile.length() / 100;
        long process = 0;
        long localreadbytes = 0L;
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"), "iso-8859-1"));
        // 断点续传
        if (remoteSize > 0)
        {
            ftpClient.setRestartOffset(remoteSize);
            process = remoteSize / step;
            raf.seek(remoteSize);
            localreadbytes = remoteSize;
        }
        byte[] bytes = new byte[1024];
        int c;
        while ((c = raf.read(bytes)) != -1)
        {
            out.write(bytes, 0, c);
            localreadbytes += c;
            if (localreadbytes / step != process)
            {
                process = localreadbytes / step;
                System.out.println("上传进度:" + process);
                // TODO 汇报上传状态
            }
        }
        out.flush();
        raf.close();
        out.close();
        boolean result = ftpClient.completePendingCommand();
        if (remoteSize > 0)
        {
            status = result ? UploadStatusEnum.UPLOAD_FROM_BREAK_SUCCESS : UploadStatusEnum.UPLOAD_FROM_BREAK_FAILED;
        }
        else
        {
            status = result ? UploadStatusEnum.UPLOAD_NEW_FILE_SUCCESS : UploadStatusEnum.UPLOAD_NEW_FILE_FAILED;
        }

        return status;
    }

    public static void main(String[] args)
    {
        FtpClients myFtp = new FtpClients();
        try
        {
            myFtp.connect("192.168.1.245", 21, "aircom", "123456");
            // myFtp.ftpClient.makeDirectory(new
            // String("电视剧".getBytes("GBK"),"iso-8859-1"));
            // myFtp.ftpClient.changeWorkingDirectory(new
            // String("电视剧".getBytes("GBK"),"iso-8859-1"));
            // myFtp.ftpClient.makeDirectory(new
            // String("走西口".getBytes("GBK"),"iso-8859-1"));
            // System.out.println(myFtp.upload("E:\\yw.flv", "/yw.flv",5));
            // System.out.println(myFtp.upload("E:\\走西口24.mp4","/央视走西口/新浪网/走西口24.mp4"));
            System.out.println(myFtp.download("2.txt", "H:\\sfa.txt"));
            myFtp.disconnect();
        }
        catch (Exception e)
        {

            System.out.println("连接FTP出错：" + e.getMessage());
        }
    }



    public enum DownloadStatusEnum{

        @MetaData(value ="远程文件不存在")
        REMOTE_FILE_NOEXIST,

        @MetaData(value = "本地文件大于远程文件")
        LOCAL_BIGGER_REMOTE,

        @MetaData(value = "断点下载文件成功")
        DOWNLOAD_FROM_BREAK_SUCCESS,

        @MetaData(value = "断点下载文件失败")
        DOWNLOAD_FROM_BREAK_FAILED,

        @MetaData(value = "全新下载文件成功")
        DOWNLOAD_NEW_SUCCESS,

        @MetaData(value = "全新下载文件失败")
        DOWNLOAD_NEW_FAILED;

    }

    public enum UploadStatusEnum{

        @MetaData(value = "远程服务器创建目录失败")
        CREATE_DIR_FAILED,

        @MetaData(value = "远程服务器创建目录成功")
        CREATE_DIR_SUCCESS,

        @MetaData(value = "上传新文件成功")
        UPLOAD_NEW_FILE_SUCCESS,

        @MetaData(value = "上传新文件失败")
        UPLOAD_NEW_FILE_FAILED,

        @MetaData(value = "文件已存在")
        FILE_EXITS,

        @MetaData(value = "远程文件大于本地文件")
        REMOTE_BIGGER_LOCAL,

        @MetaData(value = "断点续传失败")
        UPLOAD_FROM_BREAK_SUCCESS,

        @MetaData(value = "断点续传成功")
        UPLOAD_FROM_BREAK_FAILED,

        @MetaData(value = "删除远程文件失败")
        DELETE_REMOTE_FAILED;

    }


}
