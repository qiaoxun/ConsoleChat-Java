package com.test.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.test.utils.CloseUtils;

/**
 * server
 *
 */
public class Server {
	private static List<ClientReceive> clientList;
	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(8888);
			Server s = new Server();
			clientList = new ArrayList<ClientReceive>();
			ExecutorService exec = Executors.newCachedThreadPool();
			while(true){
				Socket client = server.accept();
				exec.execute(s.new ClientReceive(client));
			}
		} catch (IOException e) {
			e.printStackTrace();
			CloseUtils.closeAll(server);
		}
	}
	
	class ClientReceive implements Runnable{
		private DataInputStream dis;
		private DataOutputStream dos;
		private String name;
		private boolean isRunning = true;
		public ClientReceive(Socket client){
			try {
				dis = new DataInputStream(client.getInputStream());
				dos = new DataOutputStream(client.getOutputStream());
			} catch (IOException e) {
				isRunning = false;
				e.printStackTrace();
			}
			setName();
			sendMsg("welcome");
			sendMsgToOthers("欢迎"+this.name+"进入聊天�?");
			clientList.add(this);
		}
		/**
		 * 设置名称
		 */
		public void setName(){
			this.name = getMsg();
		}
		
		/**
		 * 获取数据
		 * @return
		 */
		public String getMsg(){
			try {
				String msg = dis.readUTF();
				System.out.println(this.name+":"+msg);
				return msg;
			} catch (IOException e) {
				isRunning = false;
				e.printStackTrace();
				clientList.remove(this);
			}
			return "";
		}
		
		/**
		 * receive msg
		 */
		public void sendMsg(String msg){
			try {
				dos.writeUTF(msg);
				dos.flush();
			} catch (IOException e) {
				isRunning = false;
				e.printStackTrace();
				clientList.remove(this);
			}
		}
		
		/**
		 * send msg
		 */
		public void sendMsgToOthers(String msg){
			for(ClientReceive cr : clientList){
				if(cr != this)
					cr.sendMsg(this.name + "�?" + msg);
			}
		}
		
		/**
		 * run
		 */
		public void run() {
			while(isRunning){
				sendMsgToOthers(getMsg());
			}
		}
	}
}
