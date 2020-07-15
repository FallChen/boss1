package com.cy.compress;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CompressUtils {


	public static void main(String[] args) {
		String file ="c:\\Users\\Falk\\Desktop\\报表清单.zip";
		File file1 = new File(file);
		try {
			CompressUtils.compress(file1.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压道文件当前地址
	 * @param zipFile
	 * @throws Exception
	 */
	public static void compress(String zipFile) throws Exception{
		String parent = Paths.get(zipFile).getParent().toString();
//		File file = new File(zipFile);
//		String parent = file.getParent();
		CompressUtils.compress(zipFile,parent);
	}

	/**
	 * 解压方法
	 * @param zipFile 解压的文件
	 * @param destDir 解压的路径
	 * @throws Exception
	 */
	public static void compress(String zipFile, String destDir) throws Exception{
		List<File> result =new ArrayList<>();
		File f;
		try (ArchiveInputStream i = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, Files.newInputStream(Paths.get(zipFile)))) {
			ArchiveEntry entry = null;
			//循环遍历所有压缩文件内容
			while ((entry = i.getNextEntry()) != null) {
				//可以读取吗？不能就下一个
				if (!i.canReadEntryData(entry)) {
					continue;
				}
				//创建文件对象
				f = new File(destDir, entry.getName());
				//判断是不是文件夹
				if (entry.isDirectory()) {
					//如果是文件夹就创建这个文件夹
					if (!f.isDirectory() && !f.mkdirs()) {
						throw new IOException("创建文件夹错误：" + f);
					}
				} else {
					result.add(f);
					//如果是文件，获取父目录
					File parent = f.getParentFile();
					//验证父目录是否是存在，不存在则创建
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("创建文件夹错误： " + parent);
					}
					//创建输出流
					try (OutputStream o = Files.newOutputStream(f.toPath())) {
						IOUtils.copy(i, o);//复制数据
					}
				}
			}
		} catch (IOException | ArchiveException e) {
			e.printStackTrace();
			throw new Exception("文件解压出错！");
		}
	}


}
