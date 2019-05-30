package test;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.Date;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
public class Test {
		
	public static void main(String[] args) {
		/**
		 * cmd의 dir 입력시 값 출력 구현
		 * ProcessBuilder, Process 이용
		 * 문제1 - bufferedreader, bufferedwriter가 아닌 byte단위로 구현
		 * 문제2 - read() blocking -> 반복문 탈출 안 됨.
		 */

		try {
			ProcessBuilder pb = new ProcessBuilder("cmd");
			pb.redirectErrorStream(true);
			Process p = pb.start();
			DataOutputStream dos = new DataOutputStream(p.getOutputStream());
			String dir = "dir\r\n";
			dos.writeBytes(dir);
			dos.flush();
			InputStream is = p.getInputStream();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int n = 0;
			int offset = 0;
			int size = 0;
			byte[] checkBuffer = {13, 10, 13, 10};
			while(true) {
				n = is.read();
				if(n < 0)
					break;
				if(checkBuffer[offset] == n) {
					offset++;
					if(offset == checkBuffer.length) {
						size++;
						baos.write(n);
						offset = 0; 
						break;
					}
				} else {
					offset = 0;
				}
				size++;
				baos.write(n);
			}
			int index = 0;
			boolean ox = false; 
			byte[] path = new byte[100];
			byte[] small = null;
			while(true) {
				n = is.read();
				if(n < 0)
					break;
				if(!ox)
					path[index] = (byte)n;
				if(!ox && n == 62) {
					ox = true;
					small = new byte[index + 1];
					System.arraycopy(path, 0, small, 0, index + 1);
					size++;
					baos.write(n);
					System.out.println("@@@@" + new String(small,"utf-8"));
					continue;
				}
				if(ox) {
					if(small[offset] == n) {
						offset++;
						if(offset == small.length) {
							size++;
							baos.write(n);
							break;
						}
					} else {
						offset = 0;
					}
				}
				index++;
				size++;
				baos.write(n);
			}
			System.out.print(baos.toString("utf-8"));
			/*
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String outputLine = "";
			StringBuilder outputMessage = new StringBuilder();
			while((outputLine = br.readLine()) != null) {
				outputMessage.append(outputLine).append("\r\n");
				System.out.print(outputLine+"\r\n");
			}
			p.waitFor();
			p.destroy();
			System.out.println("!@#!@#");
			//System.out.print(outputMessage);
			*/
		
			
			
			
			
			//스샷됨.
			/*
			Date captureTime = new Date();
			StringBuilder filepath = new StringBuilder("C:/Users/yna/Desktop/");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_kkmmss");
			filepath.append(sdf.format(captureTime)).append(".bmp");
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			Robot robot = new Robot();
			BufferedImage capture = robot.createScreenCapture(screenRect);
			ImageIO.write(capture, "bmp", new File(filepath.toString()));
			*/
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}

	}

}
