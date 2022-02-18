package com.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;

public class MutipleRequestToIBM_Mqueue_ENROLL {

	public static void main(String[] args) throws JMSException {
		try {
			MQQueueConnection connection = null;
			MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
			connectionFactory.setHostName("inblr-vm-2635.eu.uis.unisys.com");
			connectionFactory.setPort(Integer.valueOf(36819));
			connectionFactory.setChannel("SYSTEM.ADMIN.SVRCONN");
			connectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			connectionFactory.setQueueManager("IMEIHU01");
			connection = (MQQueueConnection) connectionFactory.createConnection("unisys", "");
			System.out.println("Connection:" + connection);
			MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			//MQQueue queue = (MQQueue) session.createQueue("queue:///mishraSr");
			// MQQueue queue = (MQQueue) session.createQueue("queue:///phase1b.Id007Request");

		//Tiger.team.ID007Enrollment.queue
			MQQueue queue = (MQQueue) session.createQueue("queue:///V14.ID007.REQUEST.test1");
		//	MQQueue queue = (MQQueue) session.createQueue("queue:///V14.ID008.REQUEST");
			
		//	MQQueue queue = (MQQueue) session.createQueue("queue:///V14.ID007.REQUEST.test1");//Enrollment - 2437
		//	MQQueue queue = (MQQueue) session.createQueue("queue:///V14.ID008.REQUEST");//Verification - 2437
			//MQQueue queue = (MQQueue) session.createQueue("queue:///int.ID008");
			//MQQueue queue = (MQQueue) session.createQueue("queue:///IMMI.EBIS.ID007.REQUEST");//Enrollment TEST ENV
			MQQueue queue1 = (MQQueue) session.createQueue("queue:///int.reply");
			//MQQueue queue1 = (MQQueue) session.createQueue("queue:///IMMI.EBIS.ID035.REPLY");
			MQQueueSender mqQueueSender = (MQQueueSender) session.createSender(queue);
			//String imgPath = "C:\\MTOM\\UploadSimulator\\BharathID007\\BharathID007_Enroll_Face.dat";
			
			
			String imgPath ="C:\\MTOM\\UploadSimulator\\DAT_FILES\\JsonStatham.dat";//FINGER
			//String imgPath ="C:\\MTOM\\UploadSimulator\\DAT_FILES\\ID007Fusion_1.dat";//FUSION
			//String imgPath ="C:\\MTOM\\UploadSimulator\\DAT_FILES\\FaceFromFusion.dat";
			//String imgPath ="C:\\MTOM\\UploadSimulator\\DAT_FILES\\ID007_D_THUMB_SLAPS.dat";//SLAP
		//	String imgPath ="C:\\MTOM\\slaps\\Slap duplicate and finger duplicate.dat";
			
			byte[] imgData = readBinaryData(imgPath);
			String replaceString = "PER_ENROLL_FACE_ABCDE757";
		//	String replaceString = "PER_ENROLL_FUSION_A02";
			//String replaceString = "63_fusion_versec1_notchild";
		//	String replaceString = "ENROLL_SLAP_Duplicate_10";
			for (int i =1; i <=2; i++) {
				String modifiedReplaceString = "";
				modifiedReplaceString = replaceString+"_A_"+ i;
				BytesMessage textMessage = session.createBytesMessage();
				textMessage.setJMSReplyTo(queue1);
				byte[] finalData = getFinalData(imgData, replaceString, modifiedReplaceString).toByteArray();
				textMessage.writeBytes(finalData);
				System.out.println("Request ::" + i);
				System.out.println("Message Id :::" + modifiedReplaceString);
				System.out.println("Sending to queue");
				mqQueueSender.send(textMessage);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	private static byte[] readBinaryData(String imgPath) {
		FileInputStream fileInputStream = null;
		File file = null;
		byte[] bytes = null;
		try {
			file = new File(imgPath);
			bytes = new byte[(int) file.length()];
			fileInputStream = new FileInputStream(imgPath);
			fileInputStream.read(bytes);
		} catch (Exception e) {
			System.out.println("Erreur d'ouverture du fichier " + imgPath + " ");
		}

		return bytes;
	}

	private static ByteArrayOutputStream getFinalData(byte[] bytesReprocess, String replaceString,
			String modifiedReplaceString) {
		System.out.println("Entered getFinalData()");
		ByteArrayInputStream bis = new ByteArrayInputStream(bytesReprocess);
		byte[] search;
		InputStream ris = null;
		ByteArrayOutputStream bos = null;
		try {
			search = replaceString.getBytes("UTF-8");
			byte[] replacement = modifiedReplaceString.getBytes("UTF-8");
			ris = new ReplacingInputStream(bis, search, replacement);
			bos = new ByteArrayOutputStream();
			int b;
			try {
				while (-1 != (b = ris.read()))
					bos.write(b);
			} catch (IOException e) {
				System.out.println("Exception while construct Final Data " + e.getMessage());
			}
			//System.out.println(new String(bos.toByteArray()));
		} catch (UnsupportedEncodingException e) {
			System.out.println("Exception while construct Final Data " + e.getMessage());
		}
		System.out.println("Exited getFinalData()");
		return bos;
	}


	
}
