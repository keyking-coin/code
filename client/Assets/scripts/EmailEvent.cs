using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class EmailEvent : CenterEvent {

    static GameObject pref_list_summary;

    GameObject list_container;

    List<EventDelegate> needBacks = new List<EventDelegate>();
    
	void Start () {
        if (list_container == null)
        {
            list_container = transform.FindChild("list").FindChild("body").FindChild("container").gameObject;
        }
    }

	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("SendEmail");
        if (buffer != null)
        {
            DialogUtil.tip("发生成功",true,new EventDelegate(backToCenter));
        }
        buffer = MyUtilTools.tryToLogic("DeleteEmail");
        if (buffer != null)
        {
            EventDelegate okEvent = null;
            if (transform.FindChild("list").gameObject.activeSelf)
            {//在列表界面
                okEvent = new EventDelegate(refreshList);
            }
            else
            {//在详情界面
                okEvent = new EventDelegate(backToCenter);
            }
            DialogUtil.tip("删除成功",true,okEvent);
        }
	}

    public override void click()
    {
        base.click();
        refreshList();
    }

    void refreshTitle()
    {
        List<MainData.EmailBody> emails = MainData.instance.user.emails;
        for (int i = 0; i < emails.Count; i++)
        {
            MainData.EmailBody email = emails[i];
            Transform summary = list_container.transform.FindChild("email-" + email.id);
            UILabel label_title = summary.FindChild("title").GetComponent<UILabel>();
            MyUtilTools.insertStr(label_title, email.theme, 400, email.isNew == 0 ? "(未阅读)" : "(已阅读)");
        }
    }

    void refreshList()
    {
        if (pref_list_summary == null)
        {
            pref_list_summary = Resources.Load<GameObject>("prefabs/email-summary");
        }
        if (list_container == null)
        {
            list_container = transform.FindChild("list").FindChild("body").FindChild("container").gameObject;
        }
        MyUtilTools.clearChild(list_container.transform);
        list_container.transform.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        list_container.transform.parent.localPosition = new Vector3(0,50,0);
        List<MainData.EmailBody> emails = MainData.instance.user.emails;
        int len = 160;
        float starty = 420;
        for (int i = 0 ; i < emails.Count; i++)
        {
            MainData.EmailBody email = emails[i];
            GameObject summary = NGUITools.AddChild(list_container, pref_list_summary);
            summary.name = "email-" + email.id;
            summary.transform.localPosition = new Vector3(0, starty, 0);
            UILabel label_title = summary.transform.FindChild("title").GetComponent<UILabel>();
            MyUtilTools.insertStr(label_title, email.theme,400, email.isNew == 0 ? "(未阅读)" : "(已阅读)");
            UILabel label_time = summary.transform.FindChild("time").GetComponent<UILabel>();
            label_time.text = email.time;
            UISprite face = summary.transform.FindChild("icon").GetComponent<UISprite>();
            face.spriteName = email.senderIcon;
            UILabel label_sender = summary.transform.FindChild("sender").GetComponent<UILabel>();
            label_sender.text = email.senderName;
            UILabel label_content = summary.transform.FindChild("content").GetComponent<UILabel>();
            MyUtilTools.insertStr(label_content,email.content,500);
            UIButton delete_button = summary.transform.FindChild("delete").GetComponent<UIButton>();
            EventDelegate deleteEvent = new EventDelegate(this,"delete");
            deleteEvent.parameters[0] = new EventDelegate.Parameter();
            deleteEvent.parameters[0].obj = email;
            delete_button.onClick.Add(deleteEvent);
            UIButton content_button = summary.transform.FindChild("detail").GetComponent<UIButton>();
            EventDelegate contentEvent = new EventDelegate(this,"gotoDetailFrame");
            contentEvent.parameters[0] = new EventDelegate.Parameter();
            contentEvent.parameters[0].obj = email;
            content_button.onClick.Add(contentEvent);
            starty -= len;
        }
        transform.FindChild("list").FindChild("moreDelete").GetComponent<UIToggle>().value = false;
    }

    void backFromDetail()
    {
        transform.FindChild("detail").gameObject.SetActive(false);
        transform.FindChild("list").gameObject.SetActive(true);
        refreshTitle();
    }

    void gotoDetailFrame(MainData.EmailBody email)
    {
        if (email.isNew == 0)
        {//更新是否阅读标志
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("LookEmail");
            buffer.WriteLong(MainData.instance.user.id);//编号
            buffer.WriteLong(email.id);
            NetUtil.getInstance.SendMessage(buffer, false);
        }
        transform.FindChild("list").gameObject.SetActive(false);
        Transform deatail_trans = transform.FindChild("detail");
        deatail_trans.gameObject.SetActive(true);
        needBacks.Add(new EventDelegate(backFromDetail));
        UILabel label_sender = deatail_trans.FindChild("sender").GetComponent<UILabel>();
        label_sender.text = email.senderName;
        UILabel label_title = deatail_trans.FindChild("title").GetComponent<UILabel>();
        label_title.text = email.theme;
        UILabel label_content = deatail_trans.FindChild("content").GetComponent<UILabel>();
        label_content.text = email.content;
        int row = MyUtilTools.computeRow(label_content);
        float off_y = row * (label_content.fontSize + label_content.spacingX) / 2;
        label_content.transform.localPosition = new Vector3(0, 290 - off_y, 0);
        UIButton delete_button = deatail_trans.FindChild("delete").GetComponent<UIButton>();
        EventDelegate deleteEvent = new EventDelegate(this,"delete");
        deleteEvent.parameters[0] = new EventDelegate.Parameter();
        deleteEvent.parameters[0].obj = email;
        delete_button.onClick.Clear();
        delete_button.onClick.Add(deleteEvent);
        UIButton revert_button = deatail_trans.FindChild("revert").GetComponent<UIButton>();
        EventDelegate revertEvent = new EventDelegate(this, "revertEmail");
        revertEvent.parameters[0] = new EventDelegate.Parameter();
        revertEvent.parameters[0].obj = email;
        revert_button.onClick.Clear();
        revert_button.onClick.Add(revertEvent);
    }

    void revertEmail(MainData.EmailBody email)
    {
        transform.FindChild("send").gameObject.SetActive(true);
        transform.FindChild("detail").gameObject.SetActive(false);
        Transform container = transform.FindChild("send");
        UIInput input_accepter = container.FindChild("accepter").GetComponent<UIInput>();
        input_accepter.value = email.senderName;
        needBacks.Add(new EventDelegate(goBackFromRevert));
    }

    void goBackFromRevert()
    {
        transform.FindChild("detail").gameObject.SetActive(true);
        transform.FindChild("send").gameObject.SetActive(false);
    }

    public override void backToCenter()
    {
        if (needBacks.Count > 0)
        {
            int index = needBacks.Count -1;
            EventDelegate back = needBacks[index];
            back.Execute();
            needBacks.Remove(back);
            return;
        }
        base.backToCenter();
    }

    public void send()
    {
        if (!MainData.instance.user.login()){
            LoginEvent.tryToLogin();
            return;
        }
        Transform container = transform.FindChild("send");
        UIInput input_accepter = container.FindChild("accepter").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input_accepter.value))
        {
            UILabel label = input_accepter.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        if (input_accepter.value.Equals(MainData.instance.user.nikeName))
        {
            DialogUtil.tip("不能给自己发邮件");
            return;
        }
        UIInput input_theme = container.FindChild("theme").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input_theme.value))
        {
            UILabel label = input_theme.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        UIInput input_content = container.FindChild("content").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input_content.value))
        {
            UILabel label = input_content.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("SendEmail");
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteString(input_accepter.value);
        buffer.WriteString(input_theme.value);
        buffer.WriteString(input_content.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void goToSendFrameFromList()
    {
        transform.FindChild("send").gameObject.SetActive(true);
        transform.FindChild("list").gameObject.SetActive(false);
        needBacks.Add(new EventDelegate(goBackToList));
    }

    void goBackToList()
    {
        transform.FindChild("send").gameObject.SetActive(false);
        transform.FindChild("list").gameObject.SetActive(true);
    }

    void confirmDelete(MainData.EmailBody email)
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DeleteEmail");
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteString(email.id + "");
        NetUtil.getInstance.SendMessage(buffer);
    }

    void delete(MainData.EmailBody email)
    {
        EventDelegate sure = new EventDelegate(this,"confirmDelete");
        sure.parameters[0] = new EventDelegate.Parameter();
        sure.parameters[0].obj = email;
        ConfirmUtil.confirm("是否删除此邮件",sure);
    }

    void confirmDeleteMore(SendMessageEntity entity)
    {
        ConfirmUtil.TryToDispear();
        NetUtil.getInstance.SendMessage(entity.buffer);
    }

    public void deleteMore()
    {
        if (list_container != null)
        {
            string ids = "";
            Transform trans = list_container.transform;
            for (int i = 0; i < trans.childCount; i++)
            {
                Transform sun   = trans.GetChild(i);
                UIToggle toggle = sun.FindChild("delete-flag").GetComponent<UIToggle>();
                if (toggle.value)
                {
                    string[] ss = sun.name.Split("-"[0]);
                    ids += ss[1] + ",";
                }
            }
            if (!MyUtilTools.stringIsNull(ids))
            {
                SendMessageEntity entity = new SendMessageEntity();
                entity.buffer.skip(4);
                entity.buffer.WriteString("DeleteEmail");
                entity.buffer.WriteLong(MainData.instance.user.id);
                entity.buffer.WriteString(ids);
                EventDelegate sure = new EventDelegate(this, "confirmDeleteMore");
                sure.parameters[0] = new EventDelegate.Parameter();
                sure.parameters[0].obj = entity;
                ConfirmUtil.confirm("是否删除选中的邮件",sure);
            }
            else
            {
                DialogUtil.tip("请选择要删除的邮件");
            }
        }
    }

    public void cancleMore(UIToggle toggle)
    {
        toggle.value = false;
    }

    public void moreLink(UIToggle toggle)
    {
        if (list_container != null)
        {
            Transform trans = list_container.transform;
            for (int i = 0; i < trans.childCount ; i++ )
            {
                Transform sun = trans.GetChild(i).FindChild("delete");
                sun.gameObject.SetActive(toggle.value);
            }
        }
    }
}
