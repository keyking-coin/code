using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class FriendEvent : CenterEvent {

    EventDelegate backEvent = null;

    public PhotographEvent photoEvent;

    GameObject pref_message;

    GameObject pref_pass;

    GameObject pref_message_word_detail_left;

    GameObject pref_message_word_detail_right;

    GameObject pref_message_pic_detail_left;

    GameObject pref_message_pic_detail_right;

    bool init_flag = false;

    Transform messageListContainer;

    Transform passContainer;

    Transform messageDetailContainer;

    float messageStarty = 0;

    string justUseAddFriend;

    public class MessageUpdate : MonoBehaviour
    {
        public int count = 0;

        public MainData.FriendBody friend;

        public FriendEvent friendEvent;

        void Update()
        {
            count ++;
            bool flag = false;
            List<MainData.MessageBody> messages = null;
            if (count % 200 == 0)
            {
                messages = getRecentlyMessage(friend);
                if (friendEvent.messageDetailContainer.childCount <= messages.Count)
                {
                    flag = true;
                }
                else
                {
                    friendEvent.refreshMessage(friend);
                }
            }
            ByteBuffer buffer = MyUtilTools.tryToLogic("FriendMessage");
            if (buffer != null)
            {
                flag = true;
                messages = getRecentlyMessage(friend);
            }
            if (flag)
            {
                for (int i = messages.Count - 1; i >= 0; i--)
                {
                    string childName = "message-" + messages[i].id;
                    if (friendEvent.messageDetailContainer.FindChild(childName) != null)
                    {
                        continue;
                    }
                    friendEvent.initMessage(messages[i]);
                }
            }
        }
    }

    public class FriendListUpdate : MonoBehaviour
    {
        public int count = 0;

        public MainData.FriendBody friend;

        void Start()
        {

        }

        void Update()
        {
            if (count % 200 == 0)
            {
                List<MainData.MessageBody> messages = getRecentlyMessage(friend);
                UILabel content = transform.FindChild("content").GetComponent<UILabel>();
                MyUtilTools.insertStr(content,messages.Count > 0 ? messages[0].content : "暂时没有消息", 470);
                UILabel time = transform.FindChild("time").GetComponent<UILabel>();
                if (messages.Count > 0)
                {
                    string[] ss = messages[0].time.Split(" "[0]);
                    string[] ssy = ss[0].Split("-"[0]);
                    time.text = ssy[0] + "年" + ssy[1] + "月" + ssy[2] + "日";
                    if (messages[0].look == 0)
                    {
                        transform.FindChild("newFlag").gameObject.SetActive(true);
                    }
                }
                else
                {
                    time.gameObject.SetActive(false);
                }
            }
            count++;
        }
    }

    public class FriendListDelete : MonoBehaviour
    {
        public MainData.FriendBody friend;

        public FriendEvent friendEvent;

        int count = 0;

        bool isPressed = false;

        GameObject delete_obj;

        int popCount = 0;

        void Start(){
            delete_obj = transform.parent.FindChild("delete").gameObject;
            UIButton button = delete_obj.GetComponent<UIButton>();
            button.onClick.Clear();
            button.onClick.Add(new EventDelegate(doDelete));
        }

        void Update(){
            if (isPressed)
            {
                count++;
                if (count > 50)
                {
                    openPop();
                    count = 0;
                }
            }
            else
            {
                if (delete_obj.activeSelf)
                {
                    popCount--;
                    if (popCount <= 0)
                    {
                        delete_obj.SetActive(false);
                    }
                }
            }
        }

        void comfirmDelete()
        {
            ConfirmUtil.TryToDispear();
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("FriendDel");
            buffer.WriteLong(MainData.instance.user.id);
            buffer.WriteString("" + friend.fid);
            NetUtil.getInstance.SendMessage(buffer);
        }

        void doDelete()
        {
            delete_obj.SetActive(false);
            ConfirmUtil.confirm("确定删除此好友?",comfirmDelete);
        }

        void openPop()
        {
            if (delete_obj.activeSelf)
            {
                return;
            }
            delete_obj.SetActive(true);
            popCount = 500;
        }

        void OnPress(bool pressed)
        {
            isPressed = pressed;
        }
    }


	void Start () {
        init();
	}
	
	void Update (){
        if (messageListContainer.parent.parent.parent.gameObject.activeSelf)
        {   
            List<MainData.FriendBody> friends = MainData.instance.user.friends;
            int count = 0;
            for (int i = 0; i < friends.Count;)
            {
                MainData.FriendBody friend = friends[i];
                if (friend.pass == 1)
                {
                    if (friend.flag == JustRun.DEL_FLAG)
                    {
                        friends.RemoveAt(i);
                        continue;
                    }
                    count++;
                }
                i++;
            }
            if (count != messageListContainer.childCount)
            {
                refreshMain();
            }
        }
        if (passContainer.parent.parent.gameObject.activeSelf)
        {
            List<MainData.FriendBody> friends = MainData.instance.user.friends;
            int count = 0;
            for (int i = 0; i < friends.Count; )
            {
                MainData.FriendBody friend = friends[i];
                if (friend.pass == 0)
                {
                    if (friend.flag == JustRun.DEL_FLAG)
                    {
                        friends.RemoveAt(i);
                        continue;
                    }
                    count++;
                }
                i++;
            }
            if (count != passContainer.childCount)
            {
                refreshNew();
            }
            if (MyUtilTools.tryToLogic("FriendPass") != null || count == 0)
            {
                backToCenter();
            }
        }
        ByteBuffer buffer = MyUtilTools.tryToLogic("FriendDel");
        if (buffer != null)
        {
            string tip = buffer.ReadString();
            DialogUtil.tip(tip,true);
        }
        buffer = MyUtilTools.tryToLogic("FriendApply");
        if (buffer != null)
        {
            string tip = buffer.ReadString();
            DialogUtil.tip(tip,true);
        }
        buffer = MyUtilTools.tryToLogic("FriendSearch");
        if (buffer != null)
        {
            string result = buffer.ReadString();
            openSearchResult(result,null);
        }
        
	}

    void refreshMain()
    {
        Transform list = transform.FindChild("list");
        Transform body = list.FindChild("body");
        Transform container = body.FindChild("container");
        body.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        body.localPosition = Vector3.zero;
        UILabel countLabel = container.FindChild("new").FindChild("Label").GetComponent<UILabel>();
        countLabel.text = "新朋友([00ff00]" + getNewCount() +"[-])";
        MyUtilTools.clearChild(messageListContainer);
        List<MainData.FriendBody> friends = getListByType(1);
        float starty = 0 ,len = 100;
        //好友数据
        for (int i = 0; i < friends.Count; i++)
        {
            MainData.FriendBody friend = friends[i];
            GameObject message_obj = NGUITools.AddChild(messageListContainer.gameObject, pref_message);
            message_obj.name = "message" + i;
            message_obj.AddComponent<FriendListUpdate>().friend = friend;
            UISprite icon = message_obj.transform.FindChild("icon").GetComponent<UISprite>();
            icon.spriteName = friend.fFace;
            FriendListDelete delete = icon.gameObject.AddComponent<FriendListDelete>();//长安删除逻辑
            delete.friendEvent = this;
            delete.friend = friend;
            UILabel name = message_obj.transform.FindChild("name").GetComponent<UILabel>();
            name.text = friend.fName;
            Transform content = message_obj.transform.FindChild("content");
            UIButton content_button = content.GetComponent<UIButton>();
            EventDelegate button_delegate = new EventDelegate(this, "openMessage");
            button_delegate.parameters[0] = new EventDelegate.Parameter();
            button_delegate.parameters[0].obj = friend;
            content_button.onClick.Add(button_delegate);
            message_obj.transform.localPosition = new Vector3(0,starty,0);
            starty -= len;
        }
    }

    void backFromMessage()
    {
        messageListContainer.parent.parent.parent.gameObject.SetActive(true);
        messageDetailContainer.parent.parent.gameObject.SetActive(false);
        transform.FindChild("title").FindChild("label").GetComponent<UILabel>().text = "我的好友";
    }

    void initMessage(MainData.MessageBody message)
    {
        GameObject pref_obj = message.type == 1 ? (message.sendId == MainData.instance.user.id ? pref_message_pic_detail_right : pref_message_pic_detail_left) : (message.sendId == MainData.instance.user.id ? pref_message_word_detail_right : pref_message_word_detail_left);
        GameObject message_obj = NGUITools.AddChild(messageDetailContainer.gameObject, pref_obj);
        message_obj.name = "message-" + message.id;
        Transform icon_tran = message_obj.transform.FindChild("icon");
        icon_tran.GetComponent<UISprite>().spriteName = message.sendFace;
        Transform time_tran = message_obj.transform.FindChild("time");
        time_tran.gameObject.SetActive(message.showTime == 1);
        if (message.showTime == 1)
        {
            UILabel time_label = time_tran.GetComponent<UILabel>();
            System.DateTime dateTime = System.DateTime.Parse(message.time);
            string showStr = null;
            //int days = System.DateTime.DaysInMonth(dateTime.Year, dateTime.Month);
            if (System.DateTime.Now.Year == dateTime.Year && System.DateTime.Now.Month == dateTime.Month && System.DateTime.Now.Day == dateTime.Day)
            {//同一天
                showStr = "今天" + MyUtilTools.numToString(dateTime.Hour) + ":" + MyUtilTools.numToString(dateTime.Minute);
            }
            else
            {
                showStr = dateTime.Year + "/" + MyUtilTools.numToString(dateTime.Month) + "/" + MyUtilTools.numToString(dateTime.Day) + " " + MyUtilTools.numToString(dateTime.Hour) + ":" + MyUtilTools.numToString(dateTime.Minute);
            }
            time_label.text = showStr;
            messageStarty -= 70;
        }
        if (message.type == 0)
        {//文字类型
            Transform content_trans = message_obj.transform.FindChild("content");
            float cx = content_trans.localPosition.x;
            Transform rect_tran = content_trans.FindChild("rect");
            UISprite rect_sprite = rect_tran.GetComponent<UISprite>();
            UILabel content = rect_tran.FindChild("value").GetComponent<UILabel>();
            content.width = 570;
            content.text = message.content;
            int row = MyUtilTools.computeRow(content);
            int height = row * 64;
            if (row == 1)
            {
                int width = (int)MyUtilTools.computeLen(content) + content.fontSize;
                rect_sprite.width = Mathf.Min(600,width);
                content.width = rect_sprite.width - content.fontSize;
            }
            rect_sprite.height = height;
            content.height = row * (content.fontSize + content.spacingX);
            float offx = rect_sprite.width / 2 + 14;
            int opration = cx > 0 ? -1 : 1;
            rect_tran.localPosition = new Vector3(opration * offx,content.fontSize - height / 2-5,0);
            message_obj.transform.localPosition = new Vector3(0,messageStarty,0);
            messageStarty -= height + 10;
        }
        else //图片
        {
            UITexture pic_texture = message_obj.transform.FindChild("content").FindChild("rect").FindChild("value").GetComponent<UITexture>();
            JustRun.Instance.loadPic(message.content,pic_texture);
            message_obj.transform.localPosition = new Vector3(0,messageStarty,0);
            messageStarty -= 590;
        }
    }

    void refreshMessage(MainData.FriendBody friend)
    {
        MyUtilTools.clearChild(messageDetailContainer);
        List<MainData.MessageBody> messages = getRecentlyMessage(friend);
        messageStarty = 450;
        for (int i = messages.Count - 1 ; i >= 0 ; i--)
        {
            MainData.MessageBody message = messages[i];
            initMessage(message);
        }
        UIPanel panel = messageDetailContainer.parent.GetComponent<UIPanel>();
        if (messages.Count > 0)
        {
            float y = 450 - messageStarty - panel.baseClipRegion.w+50;
            panel.clipOffset = new Vector2(0,-y);
            messageDetailContainer.parent.localPosition = new Vector3(0,y + 50, 0);
        }
        else
        {
            panel.clipOffset = Vector2.zero;
            messageDetailContainer.parent.localPosition = new Vector3(0,50, 0);
        }
    }

    void openMessage(MainData.FriendBody friend)
    {
        GameObject result = transform.FindChild("list").FindChild("result").gameObject;
        if (result.activeSelf)
        {
            backFromSearchReult();
        }
        backEvent = new EventDelegate(backFromMessage);
        transform.FindChild("title").FindChild("label").GetComponent<UILabel>().text = "和" + friend.fName + "的聊天";
        messageListContainer.parent.parent.parent.gameObject.SetActive(false);
        messageDetailContainer.parent.parent.gameObject.SetActive(true);
        MessageUpdate messageUpdate = messageDetailContainer.GetComponent<MessageUpdate>();
        messageUpdate.friend = friend;
        messageUpdate.count  = 0;
        //聊天内容显示
        refreshMessage(friend);
        Transform inputers = messageDetailContainer.parent.parent.FindChild("inputers");
        //发送文字逻辑
        UIButton send_button = inputers.FindChild("send").GetComponent<UIButton>();
        EventDelegate send_event = new EventDelegate(this, "sendStringMessage");
        send_event.parameters[0] = new EventDelegate.Parameter();
        send_event.parameters[0].obj = friend;
        send_button.onClick.Clear();
        send_button.onClick.Add(send_event);
        //发送图片逻辑
        send_button = inputers.FindChild("add-pic").GetComponent<UIButton>();
        send_event = new EventDelegate(this,"openPhotogapher");
        send_event.parameters[0] = new EventDelegate.Parameter();
        send_event.parameters[0].obj = friend;
        send_button.onClick.Clear();
        send_button.onClick.Add(send_event);
    }

    public void moreLink(GameObject obj, UIToggle toggle)
    {
        _init();
        for (int i = 0; i < messageListContainer.childCount; i++)
        {
            Transform sun = messageListContainer.GetChild(i).FindChild("delete-flag");
            sun.gameObject.SetActive(toggle.value);
            sun.GetComponent<UIToggle>().value = false;
        }
        obj.SetActive(toggle.value);
    }

    public void deleteMore()
    {
        
    }

    public void cancleMore(GameObject obj,UIToggle toggle)
    {
        toggle.value = false;
        obj.SetActive(false);
    }

    public static List<MainData.MessageBody> getRecentlyMessage(MainData.FriendBody friend)
    {
        List<MainData.MessageBody> temps = new List<MainData.MessageBody>();
        for (int i = 0; i < MainData.instance.user.messages.Count ; i++ )
        {
            MainData.MessageBody message = MainData.instance.user.messages[i];
            if (message.sendId == friend.fid || message.sendId == friend.uid)
            {
                temps.Add(message);
            }
        }
        temps.Sort();
        return temps;
    }

    void _init()
    {
        if (!init_flag)
        {
            pref_message = Resources.Load<GameObject>("prefabs/friend-message");
            pref_pass = Resources.Load<GameObject>("prefabs/friend-pass");
            pref_message_word_detail_left = Resources.Load<GameObject>("prefabs/message-word-left");
            pref_message_word_detail_right = Resources.Load<GameObject>("prefabs/message-word-right");
            pref_message_pic_detail_left = Resources.Load<GameObject>("prefabs/message-pic-left");
            pref_message_pic_detail_right = Resources.Load<GameObject>("prefabs/message-pic-right");
            
            passContainer = transform.FindChild("new-body").FindChild("body").FindChild("container");
            messageListContainer  = transform.FindChild("list").FindChild("body").FindChild("container").FindChild("messages");
            messageDetailContainer = transform.FindChild("message-detail").FindChild("body").FindChild("container");
            messageDetailContainer.gameObject.AddComponent<MessageUpdate>().friendEvent = this;
            init_flag = true;
        }
    }

    void init()
    {
        _init();
        //refreshMain();
    }

    public override void click()
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            return;
        }
        init();
        base.click();
    }

    void backFromNew()
    {
        transform.FindChild("list").gameObject.SetActive(true);
        transform.FindChild("new-body").gameObject.SetActive(false);
        init();
    }

    int getNewCount()
    {
        List<MainData.FriendBody> friends = MainData.instance.user.friends;
        int count = 0;
        for (int i = 0; i < friends.Count; i++)
        {
            MainData.FriendBody friend = friends[i];
            if (friend.pass == 0)
            {
                count++;
            }
        }
        return count;
    }

    List<MainData.FriendBody> getListByType(byte type)
    {
        List<MainData.FriendBody> friends = MainData.instance.user.friends;
        List<MainData.FriendBody> result = new List<MainData.FriendBody>();
        for (int i = 0; i < friends.Count; i++)
        {
            MainData.FriendBody friend = friends[i];
            if (friend.pass == type)
            {
                result.Add(friend);
            }
        }
        result.Sort();
        return result;
    }

    public void closeOther()
    {
        transform.FindChild("new-body").GetComponent<UIWidget>().alpha = 1f;
        transform.FindChild("pop").gameObject.SetActive(false);
        CameraUtil.pop(8);
    }

    void doOpenOther(MainData.FriendBody friend)
    {
        if (MyUtilTools.stringIsNull(friend.other))
        {
            return;
        }
        transform.FindChild("new-body").GetComponent<UIWidget>().alpha = 0.2f;
        Transform pop = transform.FindChild("pop");
        pop.gameObject.SetActive(true);
        pop.GetComponent<JustChangeLayer>().change(10);
        CameraUtil.push(8,2);
        UILabel content = pop.FindChild("content").GetComponent<UILabel>();
        content.text = friend.other;
    }

    void doFriendRefuse(MainData.FriendBody friend)
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("FriendPass");
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteLong(friend.fid);
        buffer.WriteByte(0);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void doFriendPass(MainData.FriendBody friend)
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("FriendPass");
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteLong(friend.fid);
        buffer.WriteByte(1);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void refreshNew()
    {
        List<MainData.FriendBody> friends = getListByType(0);
        passContainer.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        passContainer.parent.localPosition = Vector3.zero;
        MyUtilTools.clearChild(passContainer);
        float starty = 520 , len = 100;
        for (int i = 0; i < friends.Count; i++)
        {
            MainData.FriendBody friend = friends[i];
            GameObject pass_obj = NGUITools.AddChild(passContainer.gameObject,pref_pass);
            pass_obj.name = "pass" + i;
            UISprite icon = pass_obj.transform.FindChild("icon").GetComponent<UISprite>();
            icon.spriteName = friend.fFace;
            UILabel name = pass_obj.transform.FindChild("name").GetComponent<UILabel>();
            name.text = friend.fName;
            UILabel other = pass_obj.transform.FindChild("other").GetComponent<UILabel>();
            MyUtilTools.insertStr(other, friend.other, 250);
            UIButton other_button = other.GetComponent<UIButton>();
            EventDelegate other_event = new EventDelegate(this,"doOpenOther");
            other_event.parameters[0] = new EventDelegate.Parameter();
            other_event.parameters[0].obj = friend;
            other_button.onClick.Add(other_event);
            UIButton sure = pass_obj.transform.FindChild("sure").GetComponent<UIButton>();
            EventDelegate sure_event = new EventDelegate(this,"doFriendPass");
            sure_event.parameters[0] = new EventDelegate.Parameter();
            sure_event.parameters[0].obj = friend;
            sure.onClick.Add(sure_event);
            UIButton cancle = pass_obj.transform.FindChild("cancle").GetComponent<UIButton>();
            EventDelegate cancle_event = new EventDelegate(this,"doFriendRefuse");
            cancle_event.parameters[0] = new EventDelegate.Parameter();
            cancle_event.parameters[0].obj = friend;
            cancle.onClick.Add(cancle_event);
            UILabel time = pass_obj.transform.FindChild("time").GetComponent<UILabel>();
            time.text = friend.time;
            pass_obj.transform.localPosition = new Vector3(0,starty,0);
            starty -= len;
        }
    }

    public void openNew()
    {
        if (getListByType(0).Count == 0)
        {
            return;
        }
        transform.FindChild("list").gameObject.SetActive(false);
        transform.FindChild("new-body").gameObject.SetActive(true);
        backEvent = new EventDelegate(backFromNew);
        refreshNew();
    }

    void backFromFind()
    {
        transform.FindChild("list").gameObject.SetActive(true);
        transform.FindChild("find-body").gameObject.SetActive(false);
        init();
    }

    public void openFind()
    {
        transform.FindChild("list").gameObject.SetActive(false);
        transform.FindChild("find-body").gameObject.SetActive(true);
        backEvent = new EventDelegate(backFromFind);
    }

    void sendAddMessage()
    {
        GameObject result = transform.FindChild("list").FindChild("result").gameObject;
        if (result.activeSelf)
        {
            backFromSearchReult();
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("FriendApply");
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteString(justUseAddFriend);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void tryToAddFriend()
    {
        UIInput num_input = transform.FindChild("find-body").FindChild("inputer").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(num_input.value))
        {
            DialogUtil.tip("请输入手机号码或者昵称");
            return;
        }
        justUseAddFriend = num_input.value;
        sendAddMessage();
    }

    public override void backToCenter()
    {
        if (backEvent != null)
        {
            backEvent.Execute();
            backEvent = null;
            return;
        }
        base.backToCenter();
    }

    public void sendStringMessage(MainData.FriendBody friend)
    {
        UIInput input = transform.FindChild("message-detail").FindChild("inputers").FindChild("inputer").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip("不能发送空的消息");
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("FriendMessage");
        buffer.WriteLong(MainData.instance.user.id);//自己编号
        buffer.WriteLong(friend.fid);//好友编号
        buffer.WriteByte(0);//0 文字内容，1图片
        buffer.WriteString(input.value);//内容
        NetUtil.getInstance.SendMessage(buffer);
        input.value = "";
    }

    void sendPictureFail()
    {
        LoadUtil.show(false);
        DialogUtil.tip("图片发送失败");
    }

    void sendPictureOk(SendMessageEntity entity)
    {
        LoadUtil.show(false);
        NetUtil.getInstance.SendMessage(entity.buffer);
    }

    void sendPicture(Texture2D texture,MainData.FriendBody friend)
    {
        LoadUtil.show(true,"图片发送中请稍后");
        System.DateTime now = System.DateTime.Now;
        string dateStr = now.Year + "-" + now.Month + "-" + now.Day + " " + now.Hour + "-" + now.Minute + "-" + now.Second;
        string picName = "[" + MainData.instance.user.id + "," + friend.fid + "] " + dateStr + ".jpg";
        SendMessageEntity entity = new SendMessageEntity();
        entity.buffer.skip(4);
        entity.buffer.WriteString("FriendMessage");
        entity.buffer.WriteLong(MainData.instance.user.id);//自己编号
        entity.buffer.WriteLong(friend.fid);//好友编号
        entity.buffer.WriteByte(1);//0 文字内容，1图片
        entity.buffer.WriteString(picName);//上传的文件名字
        EventDelegate ok_event = new EventDelegate(this,"sendPictureOk");
        ok_event.parameters[0] = new EventDelegate.Parameter();
        ok_event.parameters[0].obj = entity;
        JustRun.Instance.upLoadPic(picName,texture.EncodeToJPG(),ok_event,new EventDelegate(sendPictureFail));
    }

    public void openPhotogapher(MainData.FriendBody friend)
    {
        EventDelegate event_delegate = new EventDelegate(this,"sendPicture");
        event_delegate.parameters[0] = new EventDelegate.Parameter();
        event_delegate.parameters[1] = new EventDelegate.Parameter();
        event_delegate.parameters[1].obj = friend;
        photoEvent.open(event_delegate);
    }

    public void backFromSearchReult()
    {
        CameraUtil.pop(8);
        GameObject result = transform.FindChild("list").FindChild("result").gameObject;
        result.SetActive(false);
        transform.FindChild("list").FindChild("body").GetComponent<UIPanel>().alpha = 1f;
    }

    void openSearchResult(string resultStr,MainData.FriendBody friend)
    {
        string[] ss = resultStr.Split(","[0]);
        int type = int.Parse(ss[0]);
        if (type == 0)
        {
            DialogUtil.tip("找不到用户");
            return;
        }
        GameObject result = transform.FindChild("list").FindChild("result").gameObject;
        result.SetActive(true);
        transform.FindChild("list").FindChild("body").GetComponent<UIPanel>().alpha = 0.2f;
        result.GetComponent<JustChangeLayer>().change(10);
        CameraUtil.push(8,2);
        Transform sure = result.transform.FindChild("bg").FindChild("down").FindChild("sure");
        UIButton sure_button = sure.GetComponent<UIButton>();
        UILabel sure_label   = sure.GetComponent<UILabel>();
        UILabel content = result.transform.FindChild("content").GetComponent<UILabel>();
        if (type == 1)
        {
            sure_label.text = "添加好友";
            sure_button.onClick.Clear();
            justUseAddFriend = ss[1];
            content.text = ss[1];
            sure_button.onClick.Add(new EventDelegate(sendAddMessage));
        }
        else if (type == 2)
        {
            sure_label.text = "聊  天";
            sure_button.onClick.Clear();
            if (friend.pass == 0)
            {
                sure_button.enabled = false;
                content.text = ss[1] + "(已申请,对方尚未通过)";
            }
            else
            {
                content.text = ss[1];
                EventDelegate chat_event = new EventDelegate(this,"openMessage");
                chat_event.parameters[0] = new EventDelegate.Parameter();
                chat_event.parameters[0].obj = friend;
                sure_button.onClick.Add(chat_event);
            }
        }
    }

    public void search()
    {
        UIInput input = transform.FindChild("list").FindChild("body").FindChild("container").FindChild("search").FindChild("inputer").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip("请输入要查找的用户的昵称或者手机号");
            return;
        }
        bool needNet = true;
        List<MainData.FriendBody> friends = MainData.instance.user.friends;
        MainData.FriendBody target = null;
        for (int i = 0; i < friends.Count; i++)
        {
            MainData.FriendBody friend = friends[i];
            if (friend.fName.Equals(input.value) || friend.account.Equals(input.value))
            {
                needNet = false;
                target = friend;
                break;
            }
        }
        if (needNet)
        {
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("FriendSearch");
            buffer.WriteString(input.value);//内容
            NetUtil.getInstance.SendMessage(buffer);
        }
        else
        {
            string resultStr = "2" + "," + target.fName;
            openSearchResult(resultStr,target);
        }
    }
}
