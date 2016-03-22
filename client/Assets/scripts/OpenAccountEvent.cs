using UnityEngine;
using System.Collections;
using System;

public class OpenAccountEvent : CenterEvent {

    long startTime = 0;

    //UIButton tokenButton;

    bool isUploaded = false;

	// Use this for initialization
	void Start () {

	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("OpenAccountApply");
        if (buffer != null)
        {
            isUploaded = false;
            DialogUtil.tip("申请成功", true, new EventDelegate(backToCenter));
        }
        /*
        buffer = MyUtilTools.tryToLogic("Token");
        if (buffer != null)
        {
            startTime = DateTime.Now.Ticks / 10000000;
            tokenButton.SetState(UIButtonColor.State.Disabled,true);
        }
        if (startTime > 0)
        {
            long now = DateTime.Now.Ticks / 10000000;
            int fix = (int)(now - startTime);
            if (fix >= 60)
            {
                initPhoneSend();
            }
            else
            {
                tokenLabe.text = "重发(" + MyUtilTools.numToString(60 - fix) + ")秒";
            }
        }*/
	}

    public void upload(GameObject container)
    {
        UIPopupList wjs_list = container.transform.FindChild("wjs").GetComponent<UIPopupList>();
        UIPopupList yhk_list = container.transform.FindChild("yhk").GetComponent<UIPopupList>();
        UIInput tel_input    = container.transform.FindChild("tel").GetComponent<UIInput>();
        UIInput email_input  = container.transform.FindChild("email").GetComponent<UIInput>();
        //UIInput token_input = container.transform.FindChild("token").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(tel_input.value))
        {
            DialogUtil.tip("请输入开户手机号码");
            return;
        }
        if (tel_input.value.Length != 11 )
        {
            DialogUtil.tip("手机号码尾数不对");
            return;
        }
        if (!MyUtilTools.checkEmail(email_input.value))
        {
            DialogUtil.tip("请输入合法的email");
            return;
        }
        /*
        if (MyUtilTools.checkEmail(token_input.value))
        {
            DialogUtil.tip("请输入验证码");
            return;
        }*/
        Transform indent_trans = container.transform.FindChild("indents");
        UITexture indent_front_texture = indent_trans.FindChild("front").FindChild("context").GetComponent<UITexture>();
        if (indent_front_texture.mainTexture == null)
        {
            DialogUtil.tip("请上传身份证正面");
            return;
        }
        UITexture indent_back_texture = indent_trans.FindChild("back").FindChild("context").GetComponent<UITexture>();
        if (indent_back_texture.mainTexture == null)
        {
            DialogUtil.tip("请上传身份证反面");
            return;
        }
        UITexture bank_texture = container.transform.FindChild("bank").FindChild("front").FindChild("context").GetComponent<UITexture>();
        if (bank_texture.mainTexture == null)
        {
            DialogUtil.tip("请上传银行卡正面");
            return;
        }
        SendMessageEntity entity = new SendMessageEntity();
        System.DateTime tody = System.DateTime.Now;
        string dateStr = tody.Year + "-" + tody.Month + "-" + tody.Day + "-" + tody.Hour + "-" + tody.Minute + "-" + tody.Second + ".jpg";
        entity.names.Add(tel_input.value + "-indent-front-" + dateStr);
        entity.names.Add(tel_input.value + "-indent-back-" + dateStr);
        entity.names.Add(tel_input.value + "-bank-front-" + dateStr);
        entity.buffer.skip(4);
        entity.buffer.WriteString("OpenAccountApply");
        entity.buffer.WriteString(wjs_list.value);
        entity.buffer.WriteString(yhk_list.value);
        entity.buffer.WriteString(tel_input.value);
        entity.buffer.WriteString(email_input.value);
        //entity.buffer.WriteString(token_input.value);
        entity.buffer.WriteString(entity.names[0]);
        entity.buffer.WriteString(entity.names[1]);
        entity.buffer.WriteString(entity.names[2]);
        if (!isUploaded)
        {
            EventDelegate ok = new EventDelegate(this, "uploadPicOk");
            ok.parameters[0] = new EventDelegate.Parameter();
            ok.parameters[0].obj = entity;
            ok.parameters[1] = new EventDelegate.Parameter();
            ok.parameters[1].obj = indent_back_texture;
            ok.parameters[2] = new EventDelegate.Parameter();
            ok.parameters[2].obj = bank_texture;
            LoadUtil.show(true, "上传图片中请稍后");
            JustRun.Instance.upLoadPic(entity.names[0], ((Texture2D)indent_front_texture.mainTexture).EncodeToJPG(), ok, new EventDelegate(uploadPicFail));
        }
        else
        {
            sendMessage(entity);
        }
    }

    void uploadPicOk(SendMessageEntity entity, UITexture texture1 , UITexture texture2)
    {
        if (texture1 != null && texture2 != null)
        {
            EventDelegate ok = new EventDelegate(this,"uploadPicOk");
            ok.parameters[0] = new EventDelegate.Parameter();
            ok.parameters[0].obj = entity;
            ok.parameters[1] = new EventDelegate.Parameter();
            ok.parameters[1].obj = texture2;
            ok.parameters[2] = new EventDelegate.Parameter();
            ok.parameters[2].obj = null;
            JustRun.Instance.upLoadPic(entity.names[1], ((Texture2D)texture1.mainTexture).EncodeToJPG(), ok, new EventDelegate(uploadPicFail));
        }
        else if (texture1 != null && texture2 == null)
        {
            EventDelegate ok = new EventDelegate(this,"uploadPicOk");
            ok.parameters[0] = new EventDelegate.Parameter();
            ok.parameters[0].obj = entity;
            ok.parameters[1] = new EventDelegate.Parameter();
            ok.parameters[1].obj = null;
            ok.parameters[2] = new EventDelegate.Parameter();
            ok.parameters[2].obj = null;
            JustRun.Instance.upLoadPic(entity.names[2], ((Texture2D)texture1.mainTexture).EncodeToJPG(), ok, new EventDelegate(uploadPicFail));
        }
        else
        {
            isUploaded = true;
            LoadUtil.show(false);
            EventDelegate next = new EventDelegate(this,"sendMessage");
            next.parameters[0] = new EventDelegate.Parameter();
            next.parameters[0].obj = entity;
            DialogUtil.tip("图片上传成功点击确定继续",true,next);
        }
    }

    void uploadPicFail()
    {
        LoadUtil.show(false);
        DialogUtil.tip("上传图片失败");
    }

    void sendMessage(SendMessageEntity entity)
    {
        NetUtil.getInstance.SendMessage(entity.buffer);
    }

    public void send(UIInput input)
    {
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip("请输入手机号码");
            return;
        }
        if (startTime == 0)
        {
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("Token");
            buffer.WriteString(input.value);
            buffer.WriteString("1");
            NetUtil.getInstance.SendMessage(buffer);
        }
    }

    void initPhoneSend()
    {
        //startTime = 0;
        //tokenLabe.text = "发送短信";
        //float r = (float)(43 / 255);
        //float g = (float)(130 / 255);
        //tokenLabe.color = new Color(r, g, 1f, 1);
        //tokenButton.SetState(UIButtonColor.State.Normal,true);
    }

    public override void click()
    {
        base.click();
        Transform container_trans = transform.FindChild("scroll").FindChild("body").FindChild("container");
        Transform indent_trans = container_trans.FindChild("indents");
        indent_trans.FindChild("front").FindChild("context").GetComponent<UITexture>().mainTexture = null;
        indent_trans.FindChild("back").FindChild("context").GetComponent<UITexture>().mainTexture = null;
        container_trans.FindChild("bank").FindChild("front").FindChild("context").GetComponent<UITexture>().mainTexture = null;
    }
}
