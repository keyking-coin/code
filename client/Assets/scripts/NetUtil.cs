using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Collections;
using System.Threading;
using System.Collections.Generic;

public class NetUtil{

	private static NetUtil instance = null;

	private Socket socket = null;

	byte[] buffers = new byte[1024 * 1024];

	Dictionary<string,ByteBuffer> receives = new Dictionary<string, ByteBuffer>();

    List<ByteBuffer> modules = new List<ByteBuffer>();

	List<ByteBuffer> picCachs = new List<ByteBuffer>();

	bool isReceiveMsg = false;

    string currentKey;

    bool isReceiving = true;

	public bool mustLogin = false;

    Thread receiveThread = null;

#if UNITY_EDITOR
    //string URL = "127.0.0.1";
    string URL = "139.196.30.53";
    //string URL = "keyking-ty.xicp.net:11240";
#else
    //string URL = "139.196.30.53";
    string URL = "127.0.0.1";
#endif

    int port = 32105;

	public static NetUtil getInstance
    {
        get{
            if (instance == null) {
			    instance = new NetUtil();
			    instance.connect();
		    }
            return instance;
        }
	}

    public List<ByteBuffer> Module
    {
        get
        {
            return modules;
        }
    }

	public List<ByteBuffer> Cachs
	{
		get
		{
			return picCachs;
		}
	}

	public NetUtil(){

	}

	public bool connect(){
        if (socket != null && socket.Connected)
        {
			return true;
		}
        if (receiveThread != null)
        {
            receiveThread.Abort();
            receiveThread = null;
            socket.Close();
            socket = null;
        }
        socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
		//IPAddress ipAddress = IPAddress.Parse(URL);
        String url = Dns.GetHostEntry("keyking-ty.xicp.net").AddressList[0].ToString();
        IPAddress ipAddress = IPAddress.Parse(url);
		//IPEndPoint ipEndpoint = new IPEndPoint(ipAddress,port);
        IPEndPoint ipEndpoint = new IPEndPoint(ipAddress,12213);
		IAsyncResult result = socket.BeginConnect(ipEndpoint,null,socket);
		if (result != null){
			bool success = result.AsyncWaitHandle.WaitOne(5000,true);
			if (success){
				receiveThread = new Thread(new ThreadStart(ReceiveSorket));  
				receiveThread.IsBackground = true;  
				receiveThread.Start();
        //Loom.RunAsync(ReceiveSorket);
			}
		}
		return false;
	}

	public void tryToReconnect(){
		ConfirmUtil.TryToDispear();
		connect ();
	}

	public void ReceiveSorket() {
        while (isReceiving)
        {
            if (!socket.Connected)
            {
                socket.Close();
                break;
            }
            try
            {
				ByteBuffer buffer = null;
				int datalen = 0 , templen = 0;
			    COMEMN:
				int len = socket.Receive(buffers);
                if (len > 0)
                {
					if (buffer == null)
					{
						buffer = ByteBuffer.Allocate(len);
						buffer.WriteBytes(buffers,0,len);
						datalen = buffer.ReadInt();
						templen = datalen - len + 4;
					}
					else
					{
						buffer.WriteBytesTo(buffers,len);
						templen -= len;
					}
					Array.Clear(buffers,0,buffers.Length);
					if (templen > 0)
					{
						Thread.Sleep(100);//休眠1秒继续接受没接受完的数据流
						goto COMEMN;
					}
                }
				if (buffer != null)
				{
					isReceiveMsg = true;
					string key = buffer.ReadString();
					Debug.Log("receive<" + key + ">:" + datalen + "B");
					if (key.Equals("LoginAgain"))
					{
						mustLogin = true;
						receives.Clear();
					}
					else if (key.Equals("Module"))
					{//模块数据
						modules.Add(buffer);
					}
					else if (key.Equals("LoadPic")){
						picCachs.Add(buffer);
					}
					else
					{
						receives.Add(key,buffer);
					}
				}
            }
            catch (Exception e)
            {
                Debug.LogException(e);
            }
        }
	}

	public void SendMessage(ByteBuffer buffer,bool needShowLoading = true){
		buffer.WriteInt(0,buffer.getWriteIndex()-4);
		byte[] arrays = buffer.ToArray();
        buffer.ReadInt();
		currentKey = needShowLoading ? buffer.ReadString() : null;
		SendMessage (arrays,needShowLoading);
	}

	public void SendMessage(byte[] datas,bool needShowLoading){
		if (!socket.Connected)  {
			ConfirmUtil.confirm("未连接服务器,连接?",tryToReconnect);
			return;
		}
		try  {  
			LoadUtil.show(needShowLoading);
			socket.BeginSend(datas,0,datas.Length,SocketFlags.None,null,socket);
		}  catch  {
			Debug.Log("send message error");  
		}
	}

	//关闭Socket
	public void Closed(){
		if (socket != null && socket.Connected){
			socket.Shutdown(SocketShutdown.Both);
			socket.Close();
		}
        isReceiving = false;
	}

    public ByteBuffer find(string key)
    {
        if (!isReceiveMsg || currentKey == null || !key.Equals(currentKey))
        {
			return null;
		}
        if (receives.ContainsKey(key))
        {
            return receives[currentKey];
        }
        return null;
	}


	public void remove(string key){
        LoadUtil.show(false);
		isReceiveMsg = false;
		receives.Remove (key);
        currentKey = null;
	}

    public void clear()
    {
        if (currentKey != null)
        {
            remove(currentKey);
        }
    }

    public string cKey
    {
        get
        {
            return currentKey;
        }
        set
        {
            currentKey = value;
        }
    }
}

 
 
