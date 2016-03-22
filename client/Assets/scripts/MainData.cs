using UnityEngine;
using System.Collections.Generic;
using LitJson;

public class MainData{
    public class Forbid
    {
        public string reason;
        public string endTime;//forever永久封号，null正常状态,"2016-05-03 00:00:00"表示封号截止时间。
        public void deserialize(ByteBuffer buffer)
        {
            reason = buffer.ReadString();
            endTime = buffer.ReadString();
        }
    }
    public class Credit{
        public float curValue;//已使用额度
        public float maxValue = 100000;//最大信用额度,每个人一开始就10万额度
        public float tempMaxValue = 100000;//临时信用额度
        public float totalDealValue;//总的成交金额
        public int hp;//好评次数
        public int zp;//中评次数
        public int cp;//差评次数
        public void deserialize(ByteBuffer buffer)
        {
            curValue = float.Parse(buffer.ReadString());
            maxValue = float.Parse(buffer.ReadString());
            tempMaxValue = float.Parse(buffer.ReadString());
            totalDealValue = float.Parse(buffer.ReadString());
            hp = buffer.ReadInt();
            zp = buffer.ReadInt();
            cp = buffer.ReadInt();
        }
    }
    public class SimpleOrderModule : Object
    {
        public long dealId;
        public long orderId;
        public string content;
        public string time;
        public void deserialize(ByteBuffer buffer)
        {
            dealId  = buffer.ReadLong();
            orderId = buffer.ReadLong();
            content = buffer.ReadString();
            time    = buffer.ReadString();
        }
    }

	public class Seller
	{
		public bool have;
		public string time;
		public byte type;
		public string key;
		public string picName;
		public float deposit;
		public bool pass;
		public void deserialize(ByteBuffer buffer)
		{
			have = buffer.ReadByte() == 1;
			if (!have) return;
			time = buffer.ReadString();
			type = buffer.ReadByte();
			key = buffer.ReadString();
			picName = buffer.ReadString();
			deposit = float.Parse(buffer.ReadString());
			pass = buffer.ReadByte() == 1;
		}
	}

    public class RechargeOrder//充值订单
    {
		public string time;//充值时间
		public float value;//充值数据
		public byte type;//充值途径 0 银联、1支付宝、2微信。

        public void deserialize(ByteBuffer buffer)
        {
			type  = buffer.ReadByte ();
			value = float.Parse (buffer.ReadString());
			time  = buffer.ReadString ();
        } 
    }

    public class Recharge//充值系统
    {
        public float curMoney = 10000;

		public float historyMoney;//历史累计充值

		public List<RechargeOrder> orders = new List<RechargeOrder>();//充值订单列表

        public void deserialize(ByteBuffer buffer)
        {
            curMoney = float.Parse(buffer.ReadString());
            historyMoney = float.Parse(buffer.ReadString());
            orders.Clear();
            int len = buffer.ReadInt();
            for (int i = 0; i < len; i++ )
            {
                RechargeOrder order = new RechargeOrder();
                order.deserialize(buffer);
                orders.Add(order);
            }
        }

        public bool haveMoney(float money)
        {
            return curMoney > money;
        }

    }

    public class BankAccount : Object
    {
        public List<string> names = new List<string>();
        public List<string> accounts = new List<string>();
        public List<string> openAddresses = new List<string>();//开户行
        public List<string> openNames = new List<string>();//开户人

        public void deserializeModule(ByteBuffer buffer)
        {
            buffer.ReadByte();
            clear();
            int len = buffer.ReadInt();
            for (int i = 0; i < len; i++)
            {
                names.Add(buffer.ReadString());
                accounts.Add(buffer.ReadString());
                openAddresses.Add(buffer.ReadString());
                openNames.Add(buffer.ReadString());
            }
        }

        public void deserialize(ByteBuffer buffer)
        {
			clear();
            int len  = buffer.ReadInt();
            for (int i = 0; i < len; i++ )
            {
                names.Add(buffer.ReadString());
                accounts.Add(buffer.ReadString());
                openAddresses.Add(buffer.ReadString());
                openNames.Add(buffer.ReadString());
            }
        }

        public void clear()
        {
            names.Clear();
            accounts.Clear();
            openAddresses.Clear();
            openNames.Clear();
        }
    }

    public class EmailBody : Object
    {
        public byte type;
        public long id;
        public byte status;
        public long senderId;
        public UserData user;
        public string time;// 发送时间
        public string theme = "";// 主题
        public string content = "";// 内容
        public string senderName = "系统";//发送者名字
        public string senderIcon = "";//发生者头像
        public byte isNew;

        public EmailBody(UserData user)
        {
            this.user = user;
        }

        public void deserialize(ByteBuffer buffer,bool needSkip = false)
        {
            if (!needSkip)
            {
                id = buffer.ReadLong();
            }
            type = buffer.ReadByte();
            status = buffer.ReadByte();
            senderId = buffer.ReadLong();
            time = buffer.ReadString();
            theme = buffer.ReadString();
            content = buffer.ReadString();
            senderName = buffer.ReadString();
            senderIcon = buffer.ReadString();
            isNew = buffer.ReadByte();
        }
    }

    public class FriendBody : Object, System.IComparable<FriendBody>
    {
        public long uid;//我的编号
        public long fid;//朋友编号
        public string account;//朋友账号
        public string fName;//朋友昵称
        public string fFace;//朋友头像
        public byte pass;//是否通过 0 申请,1通过。
        public string time;//申请时间
        public string other;//验证信息
        public byte flag = JustRun.NULL_FLAG;//标志

        public void deserialize(ByteBuffer buffer)
        {
            uid = buffer.ReadLong();
            fid = buffer.ReadLong();
            account = buffer.ReadString();
            fName = buffer.ReadString();
            fFace = buffer.ReadString();
            pass = buffer.ReadByte();
            time = buffer.ReadString();
            other = buffer.ReadString();
        }

        public void copy(FriendBody body)
        {
            uid = body.uid;
            fid = body.fid;
            fName = body.fName;
            fFace = body.fFace;
            pass = body.pass;
            time = body.time;
            other = body.other;
            flag = body.flag;
        }

        public int CompareTo(FriendBody other)
        {
            System.DateTime mt = System.DateTime.Parse(time);
            System.DateTime tt = System.DateTime.Parse(other.time);
            return System.DateTime.Compare(tt,mt);
        }

    }

    public class MessageBody : Object, System.IComparable<MessageBody>
    {
        public long id;
        public long sendId;//发送者编号
        public string sendFace;//发送者头像
        public string time;//发送时间
        public string content;//发送内容
        public byte type;//内容的类型0文字,1图片(暂时只支持两类后面可能要扩展)
        public byte look;//是否看过了0未看,1看过了
        public byte showTime;//0不显示，1显示

        public void deserialize(ByteBuffer buffer)
        {
            id = buffer.ReadLong();
            sendId = buffer.ReadLong();
            sendFace = buffer.ReadString();
            type = buffer.ReadByte();
            look = buffer.ReadByte();
            showTime = buffer.ReadByte();
            time = buffer.ReadString();
            content = buffer.ReadString();
        }

        public void copy(MessageBody body)
        {
            id = body.id;
            sendId = body.sendId;
            sendFace = body.sendFace;
            type = body.type;
            look = body.look;
            time = body.time;
            content = body.content;
        }

        public int CompareTo(MessageBody other)
        {
            System.DateTime mt = System.DateTime.Parse(time);
            System.DateTime tt = System.DateTime.Parse(other.time);
            return System.DateTime.Compare(tt,mt);
        }

    }

    public class UserData
    {
        public long id = 0;
        public string account = "uu_admin_001";
        public string face = "face1";
        public string nikeName = "小游";
        public string endTime;
        public int permission;
        public string realyName = "王五";
        public string title = "普通会员";
        public string registTime = "2015-06-25";
        public string indentity = "325647844455412545";
        public string signature = "热爱自由";
        public bool pushFlag = true;
        public List<string> addresses = new List<string>();
        public Recharge recharge = new Recharge();
        public BankAccount bacnkAccount = new BankAccount();
		public Seller seller = new Seller ();
        public List<FriendBody> friends = new List<FriendBody>();
        public List<MessageBody> messages = new List<MessageBody>();
        public List<SimpleOrderModule> recentOrders = new List<SimpleOrderModule>();
        public bool simpleOrderModuleNeedRefresh = false;
        public List<long> favorites = new List<long>();//收藏的帖子
        public Credit credit = new Credit();
        public byte breach;//违规次数
        public Forbid forbid = new Forbid();
        public string other;
        public List<EmailBody> emails = new List<EmailBody>();

        public void deserialize(ByteBuffer buffer)
        {
            id = buffer.ReadLong();
            account  = buffer.ReadString();
            face = buffer.ReadString();
            nikeName = buffer.ReadString();
            realyName  = buffer.ReadString();
            title      = buffer.ReadString();
            registTime = buffer.ReadString();
            indentity = buffer.ReadString();
            signature = buffer.ReadString();
            pushFlag = buffer.ReadByte() == 1;
            int len = buffer.ReadInt();
            addresses.Clear();
            if (len > 0)
            {
                for (int i = 0; i < len; i++)
                {
                    addresses.Add(buffer.ReadString());
                }
            }
            recharge.deserialize(buffer);
            bacnkAccount.deserialize(buffer);
			seller.deserialize (buffer);
            len = buffer.ReadInt();
            emails.Clear();
            if (len  > 0)
            {
                for (int i = 0; i < len; i++ )
                {
                    EmailBody email = new EmailBody(this);
                    email.deserialize(buffer);
                    emails.Add(email);
                }
            }
            len = buffer.ReadInt();
            friends.Clear();
            if (len > 0)
            {
                for (int i = 0; i < len; i++)
                {
                    FriendBody friend = new FriendBody();
                    friend.deserialize(buffer);
                    friends.Add(friend);
                }
            }
            len = buffer.ReadInt();
            messages.Clear();
            if (len > 0)
            {
                for (int i = 0; i < len; i++)
                {
                    MessageBody message = new MessageBody();
                    message.deserialize(buffer);
                    messages.Add(message);
                }
            }
            len = buffer.ReadInt();
            favorites.Clear();
            if (len > 0)
            {
                for (int i = 0; i < len; i++)
                {
                    long value = buffer.ReadLong();
                    favorites.Add(value);
                }
            }
            endTime    = buffer.ReadString();
            permission = buffer.ReadInt();
            credit.deserialize(buffer);
            breach = buffer.ReadByte();
            forbid.deserialize(buffer);
            other = buffer.ReadString();
        }

        public void deserializeModuleOne(ByteBuffer buffer)
        {
            buffer.ReadByte();
            deserialize(buffer);
        }

        public bool login()
        {
            return id > 0;
        }

        public bool haveNewEmail()
        {
            foreach (EmailBody email in emails)
            {
                if (email.isNew == 0)
                {
                    return true;
                }
            }
            return false;
        }

        public bool isFavorite(long dealId)
        {
            return favorites.Contains(dealId);
        }
    }

    public UserData user = new UserData();

    public static MainData instance = new MainData();


    public List<DealBody> deal_all = new List<DealBody>();

    public void deserializeDeals(ByteBuffer data)
    {
        deal_all.Clear();
        int len = data.ReadInt();
        for (int i = 0; i < len; i++)
        {
            DealBody body = DealBody.read(data);
            deal_all.Add(body);
        }
    }

    public void deserializeDealModuleOne(ByteBuffer data)
	{
		deserializeDealModule(data);
	}

    public DealBody deserializeDealModule(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        DealBody newItem = DealBody.read(data);
        if (flag == JustRun.ADD_FLAG)
        {
            deal_all.Insert(0,newItem);
            newItem.flag = flag;
        }
        else if (flag == JustRun.DEL_FLAG)
        {
            for (int i = 0; i < deal_all.Count; i++ )
            {
                DealBody item = deal_all[i];
                if (item.id == newItem.id){
                    //deal_all.RemoveAt(i);
                    item.flag = flag;
                    return item;
                }
            }
        }
        else if (flag == JustRun.UPDATE_FLAG)
        {
            for (int i = 0; i < deal_all.Count; i++)
            {
                DealBody item = deal_all[i];
                if (item.id == newItem.id)
                {
                    item.copy(newItem);
                    item.flag = flag;
                    return item;
                }
            }
        }
        return newItem;
    }

    public void deserializeOrderModuleOne(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        DealBody.Order order = DealBody.Order.read(data);
        foreach (DealBody deal in deal_all)
        {
            if (deal.id == order.dealId)
            {
                order.item = deal;
                if (flag == JustRun.ADD_FLAG)
                {
                    bool insert = true;
                    foreach ( DealBody.Order dor in deal.orders)
                    {
                        if (dor.id == order.id)
                        {
                            insert = false;
                            break;
                        }
                    }
                    if (insert)
                    {
                        deal.orders.Add(order);
                    }               
                }
                else if (flag == JustRun.DEL_FLAG)
                {
                    foreach (DealBody.Order dor in deal.orders)
                    {
                        if (dor.id == order.id)
                        {
                            deal.orders.Remove(order);
                            break;
                        }
                    }
                }
                else if (flag == JustRun.UPDATE_FLAG)
                {
                    foreach (DealBody.Order dor in deal.orders)
                    {
                        if (dor.id == order.id)
                        {
                            dor.copy(order);
                            dor.refresh = true;
                            break;
                        }
                    }
                }
                break;
            }
        }
        
    }

    public void deserializeEmailModule(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        if (flag == JustRun.ADD_FLAG)
        {
            EmailBody email = new EmailBody(user);
            email.deserialize(data);
            user.emails.Add(email);
        }
        else if (flag == JustRun.DEL_FLAG)
        {
            EmailBody email = new EmailBody(user);
            email.deserialize(data);
            foreach (EmailBody e in user.emails)
            {
                if (e.id == email.id)
                {
                    user.emails.Remove(e);
                    break;
                }
            }
        }
        else if (flag == JustRun.UPDATE_FLAG)
        {
            long eid = data.ReadLong();
            foreach (EmailBody email in user.emails )
            {
                if (email.id == eid)
                {
                    email.deserialize(data,true);
                    break;
                }
            }
        }
    }

    public void deserializeSimpleOrderModule(ByteBuffer data)
    {
        int len = data.ReadInt();
        if (len > 0)
        {
            user.recentOrders.Clear();
            for (int i = 0 ; i < len; i++)
            {
                data.ReadByte();
                data.ReadByte();
                SimpleOrderModule module = new SimpleOrderModule();
                module.deserialize(data);
                user.recentOrders.Add(module);
            }
            user.simpleOrderModuleNeedRefresh = true;
        }
    }

    public void deserializeSimpleOrderModuleOne(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        if (flag == JustRun.ADD_FLAG)
        {
            SimpleOrderModule module = new SimpleOrderModule();
            module.deserialize(data);
            if (user.recentOrders.Count >= 20)
            {
                user.recentOrders.RemoveAt(user.recentOrders.Count - 1);
            }
            user.recentOrders.Insert(0,module);
            user.simpleOrderModuleNeedRefresh = true;
        }
    }

    public void deserializeFriendModuleOne(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        if (flag == JustRun.ADD_FLAG)
        {
            FriendBody friend = new FriendBody();
            friend.deserialize(data);
            user.friends.Add(friend);
            friend.flag = flag;
        }
        else if (flag == JustRun.DEL_FLAG)
        {
            FriendBody friend = new FriendBody();
            friend.deserialize(data);
            for (int i = 0; i < user.friends.Count; i++ )
            {
                FriendBody fb = user.friends[i];
                if (fb.fid == friend.fid && friend.uid == fb.uid)
                {
                    fb.flag = flag;
                    break;
                }
            }
        }
        else if (flag == JustRun.UPDATE_FLAG)
        {
            FriendBody friend = new FriendBody();
            friend.deserialize(data);
            for (int i = 0; i < user.friends.Count; i++)
            {
                FriendBody fb = user.friends[i];
                if (fb.fid == friend.fid && friend.uid == fb.uid)
                {
                    fb.copy(friend);
                    break;
                }
            }
        }
    }

    public void deserializeMessageModuleOne(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        if (flag == JustRun.ADD_FLAG)
        {
            MessageBody message = new MessageBody();
            message.deserialize(data);
            user.messages.Add(message);
        }
        else if (flag == JustRun.DEL_FLAG)
        {
            MessageBody message = new MessageBody();
            message.deserialize(data);
            for (int i = 0; i < user.messages.Count; i++)
            {
                MessageBody mb = user.messages[i];
                if (mb.id == message.id)
                {
                    user.messages.RemoveAt(i);
                    break;
                }
            }
        }
        else if (flag == JustRun.UPDATE_FLAG)
        {
            MessageBody message = new MessageBody();
            message.deserialize(data);
            for (int i = 0; i < user.messages.Count; i++)
            {
                MessageBody mb = user.messages[i];
                if (mb.id == message.id)
                {
                    mb.copy(message);
                    break;
                }
            }
        }
    }
}
 
 
