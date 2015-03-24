package com.szboc.platform.component.communication.logger;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szboc.platform.component.communication.logger.config.ConfigHelper;

/**
 * 原始报文保存对象，负责将各种通信时发送的报文保存至指定的文件中<br>
 * 提供日志文件重命名服务<br>
 * 提供按照一定策略定期删除过期的日志报文<br>
 * 
 * @author 刺客
 * 
 */
public class MessageLogger {

	private static Logger logger = LoggerFactory.getLogger(MessageLogger.class);

	/**
	 * 日志文件保存策略
	 */
	private static final int ORI_MSG_FILE_SAVE_POLICY = ConfigHelper
			.getInstance().getLoggerConfig().getORIMSGFILESAVEPOLICY();

	/**
	 * 根据报文保存策略，删除之前的日志文件<br>
	 * 保存新增的日志文件<br>
	 * 
	 * @param newFileName
	 *            指定文件名称, 不包含目录
	 * @param dir
	 *            日志文件目录，本方法将在此目录下按照日期新建目录，按照系统当前日期切换到新的目录下
	 * @param content
	 *            原始报文信息
	 * @param charset
	 *            报文编码字符集
	 * @return
	 * @throws IOException
	 */
	public static String saveMsgFileInfo(String newFileName, String dir,
			String content, String charset) throws IOException {

		ensurePathExist(dir);

		if (!dir.endsWith("/") && !dir.endsWith("\\")) {
			dir = dir + "/";
		}
		String dirDate = getSysDateNO();

		ensurePathExist(dir, dirDate);

		// 文件对象
		File file = new File(dir + dirDate, newFileName);

		FileUtils.writeStringToFile(file, content, charset);

		return file.getAbsolutePath();
	}

	/**
	 * 确保路径存在
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static synchronized void ensurePathExist(String path)
			throws IOException {
		File file = new File(path);
		FileUtils.forceMkdir(file);
	}

	/**
	 * 确保路径下子路径存在, 并删除过期(创建日期超过<code>ORI_MSG_FILE_SAVE_POLICY</code>)子目录
	 * 
	 * @param path
	 * @param child
	 * @throws IOException
	 */
	private static synchronized void ensurePathExist(String path, String child)
			throws IOException {
		File file = new File(path, child);
		if (!file.exists()) {
			FileUtils.forceMkdir(file);

			// 删除过期的日志文件
			String delDate = getTargetDate("yyyyMMdd", getSysDateNO(),
					"yyyyMMdd", -1 * ORI_MSG_FILE_SAVE_POLICY);
			File rootDir = new File(path);
			for (File delDir : rootDir.listFiles()) {
				if (delDir.getName().compareTo(delDate) <= 0 && delDir.exists()
						&& delDir.isDirectory()) {
					try {
						FileUtils.deleteDirectory(delDir);
					} catch (IOException e) {
						logger.error("删除[" + delDir.getAbsolutePath() + "]时异常："
								+ e, e);
					}
				}
			}
		}
	}

	/**
	 * 重命名文件名
	 * 
	 * @param oldFilename
	 * @param newFilename
	 */
	public static boolean renameFilename(String oldFilename, String newFilename) {
		File oldFile = new File(oldFilename);
		File newFile = new File(newFilename);
		return oldFile.renameTo(newFile);
	}

	private static String getSysDateNO() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}

	private static String getTargetDate(String srcPattern, String srcDate,
			String targetPattern, int subDate) {
		if (targetPattern == null) {
			targetPattern = "yyyy-MM-dd";
		}
		if (srcPattern == null) {
			srcPattern = "yyyy-MM-dd";
		}
		SimpleDateFormat srcDf = new SimpleDateFormat(srcPattern);
		SimpleDateFormat tmpDf = new SimpleDateFormat("yyyy-MM-dd");
		String fDate = "";

		try {
			fDate = tmpDf.format(srcDf.parse(srcDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (!"".equals(fDate)) {
			String year = fDate.substring(0, 4);
			String month = fDate.substring(5, 7);
			String date = fDate.substring(8, 10);
			Calendar ca = Calendar.getInstance();
			// 将日期设为输入月份的第一天
			ca.set(Integer.parseInt(year), Integer.parseInt(month) - 1,
					Integer.parseInt(date) + subDate);
			SimpleDateFormat df = new SimpleDateFormat(targetPattern);
			String lmld = df.format(ca.getTime());
			return lmld;
		} else {
			return fDate;
		}
	}
}
