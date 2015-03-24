/**
 * 
 */
package com.szboc.platform.component.communication.logger.config;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.szboc.platform.component.communication.logger.config.jaxb.LOGGER;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : SZSBIC <br>
 * JDK version used : jdk1.6.0_29 <br>
 * Description : Eces配置对象实例<br>
 * 优先载入classpath更目录下的eces_config.xml<br>
 * 如果没有，在导入默认的eces.config.eces_config.xml<br>
 * Comments Name : ConfigHelper.java <br>
 * author : likw <br>
 * date : 2014-4-26 <br>
 * Version : 1.00 <br>
 * editor : <br>
 * editorDate : <br>
 */
public class ConfigHelper {

	private static Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

	private LOGGER loggerConfig = null;

	private ConfigHelper() throws JAXBException, SAXException {
		// 加载xml配置文件
		JAXBContext jaxbContext = JAXBContext
				.newInstance("com.szboc.platform.component.communication.logger.config.jaxb");

		logger.info("加载message_logger_config.xml配置文件...");
		InputStream is = this.getClass().getResourceAsStream(
				"/message_logger_config.xml");
		if (null == is) {
			is = this
					.getClass()
					.getResourceAsStream(
							"/com/szboc/platform/component/communication/logger/config/message_logger_config.xml");
		}

		Source source = new StreamSource(is);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		ValidationEventCollector handler = new ValidationEventCollector();
		unmarshaller.setEventHandler(handler);

		SchemaFactory facotry = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema _schema = facotry
				.newSchema(new StreamSource(
						this.getClass()
								.getResourceAsStream(
										"/com/szboc/platform/component/communication/logger/config/xsd/message_logger_config.xsd")));
		if (_schema != null) {
			unmarshaller.setSchema(_schema);
		}

		this.loggerConfig = (LOGGER) unmarshaller.unmarshal(source);
	}

	private static ConfigHelper self = null;

	public static ConfigHelper getInstance() {
		synchronized (ConfigHelper.class) {
			if (null == self) {
				try {
					self = new ConfigHelper();
				} catch (Exception e) {
					logger.error("ConfigHelper初始化异常:" + e, e);
				}
			}
		}

		return self;
	}

	public LOGGER getLoggerConfig() {
		return loggerConfig;
	}

	public void setLoggerConfig(LOGGER loggerConfig) {
		this.loggerConfig = loggerConfig;
	}

}
