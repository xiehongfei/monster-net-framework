package org.monster.framework.tes.common.utils.xml;

import com.google.common.collect.Lists;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.monster.framework.tes.common.annotation.MetaData;
import org.monster.framework.tes.common.utils.json.JsonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

/**
 * <pre>
 * Project :       Monster-frameWork
 * Author:         XIE-HONGFEI
 * Company:        hongfei tld.
 * Created Date:   2017/2/22 0022
 * Copyright @ 2017 Company hongfei tld. – Confidential and Proprietary
 *
 * Desc:
 *  1.基于JDK内置JAXB对JavaBean进行序列化为xml或将xml反序列化为java bean对象
 *  2.涉及实体Bean需有 @XmlRootElement 及 @XmlElement 注解
 * Note :
 *    @XmlElement注解只能应用在set方法中,如转换对象为ORM-Entity实体对象，则建议不再使用lombok进行代码处理;
 *
 *    TODO 使用fastjackson中关于xml加解密相关方法进行封装；
 * History:
 * ------------------------------------------------------------------------------
 *       Date       |       Author      |   Change Description
 * 2017/2/22 0022   |       谢洪飞       |   初版做成
 * </pre>
 */

public class XmlHelper {


    private JAXBContext jaxbContext;


    public XmlHelper( Class<?> ...classes ){

        try {
            jaxbContext = JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            e.printStackTrace();
            //TODO 封装服务级别异常,并抛出
        }
    }


    /**
     * <pre>
     *  实体Bean转XML
     *
     * @param obj  待转实体Bean
     * @return     转换结果，XML结构
     * </pre>
     */
    public String bean2Xml( Object obj ){

        return bean2Xml(obj , StringUtils.EMPTY);
    }


    /**
     * <pre>
     *  实体Bean转XML
     *
     * @param obj       待转实体
     * @param encoding  采用字符集别
     * @return          转换结果，XML结构
     * </pre>
     */
    public String bean2Xml( Object obj , String encoding){

        StringWriter writer = new StringWriter();

        try {

            if(StringUtils.isNotBlank(encoding))
                createMarshaller(encoding).marshal(obj,writer);
            else
             createMarshaller().marshal(obj , writer);

        } catch (JAXBException e) {
            e.printStackTrace();
            //TODO 封装服务级别异常,并抛出
        }

        return writer.toString();
    }


    public <T> T xml2Bean( String xmlContent ){

        T t = null;

        if (StringUtils.isBlank(xmlContent))
            return t;

        StringReader sr = new StringReader(xmlContent);
        try {
           t = (T) createUnmarshaller().unmarshal(sr);
        } catch (JAXBException e) {
            e.printStackTrace();
            //TODO 封装服务级别异常,并抛出
        }
        return t;
    }

    private Unmarshaller createUnmarshaller(){

        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
            //TODO 封装服务级别异常,并抛出
        }

        return unmarshaller;
    }





    private Marshaller createMarshaller(){
        return createMarshaller(StringUtils.EMPTY);
    }


    /**
     * Create a <tt>Marshaller</tt> object that can be used to convert a
     * java content tree into XML data.
     *
     * @return a <tt>Marshaller</tt> object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>Marshaller</tt> object
     */
    private Marshaller createMarshaller( String encoding ){

        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT , Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING , StringUtils.isNoneBlank(encoding)?encoding:"UTF-8");
        } catch (JAXBException e) {
            e.printStackTrace();
            //TODO 封装服务级别异常,并抛出
        }
            return marshaller;
    }


    /**
     * @author Administrator
     *
     */
    @Setter
    @XmlRootElement(name = "example")
    static class Example {

        private String examId;

        //针对实体属性与xml节点名称不一致的情况,可采用name指定的方式进行映射;

        private String examName;

        private List<ExampleChild> childs;

        @Override
        public String toString() {
            return "no:" + examId + "\n" + "name:" + examName;
        }

        @XmlElement
        public String getExamId() {
            return examId;
        }

        @XmlElement(name = "example_name")
        public String getExamName() {
            return examName;
        }

        //节点外层包裹 example_childs层
        @XmlElementWrapper(name = "example_childs")
        @XmlElement(name = "example_child")
        public List<ExampleChild> getChilds() {
            return childs;
        }

    }

    /**
     *
     * XmlElement作用于getter
     * @author Administrator
     *
     */
    @Setter
    static class ExampleChild {

        @MetaData(value = "子节点编号")
        private String childNo;

        @MetaData(value = "子节点名称")
        private String childName;

        @XmlElement
        public String getChildNo() {
            return childNo;
        }

        @XmlElement
        public String getChildName() {
            return childName;
        }

    }


    public static void main( String ... args ){

        XmlHelper xmlHelper = new XmlHelper(Example.class);

        Example example = new Example();

        example.setExamId("10086");
        example.setExamName("中国移动示例");

        List<ExampleChild> childs = Lists.newArrayList();
        example.setChilds(childs);
        for (int i = 0; i < 3; i++) {
            ExampleChild child = new ExampleChild();
            child.setChildNo((i + 1) + "");
            child.setChildName("子节点名称-" + i);
            childs.add(child);
        }

        System.out.println("=========================  bean2Xml 结果开始  =============================");
        System.out.println(xmlHelper.bean2Xml(example));
        System.out.println("=========================  bean2Xml 结果结束  =============================");

        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<example>\n" + "    <examId>10010</examId>\n"
                + "    <example_name>中国联通示例</example_name>\n" + "</example>";

        Example example2 = xmlHelper.xml2Bean(xmlContent);

        System.out.println("=========================  xml2Bean 结果开始  =============================");
        System.out.println(JsonUtils.writeValueAsString(example2));
        System.out.println("=========================  xml2Bean 结果开始  =============================");

    }
}
