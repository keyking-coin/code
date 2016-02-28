using UnityEngine;
using System.Collections;

public class AppraiseEvent : MonoBehaviour {

    DealBody.Order order;
    //GameObject
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("Appraise");
        if (buffer != null)
        {
            byte type = buffer.ReadByte();
            DealBody.Appraise appraise = type == 0 ? order.sellerAppraise : order.buyerAppraise;
            appraise.isCompleted = buffer.ReadByte() == 1;
            appraise.star = buffer.ReadByte();
            appraise.detail = buffer.ReadString();
            appraise.time = buffer.ReadString();
            Transform appraise_trans = transform.parent.parent.parent.FindChild("appraise");
            appraise_trans.FindChild("tips").gameObject.SetActive(true);
            appraise_trans.FindChild("do").gameObject.SetActive(false);
        }
        buffer = MyUtilTools.tryToLogic("DealOrderUpdate");
        if (buffer != null)
        {
            order.times.Clear();
            order.state = buffer.ReadByte();
            for (byte i = 0 ; i <= order.state ; i++)
            {
                string time = buffer.ReadString();
                order.times.Add(time);
            }
            order.insterToObj(gameObject);
        }
	}

    public DealBody.Order Order
    {
        set
        {
            order = value;
        }
    }

    void backFromAppraise(GameObject container)
    {
        container.SetActive(false);
        CameraUtil.pop(3);
        transform.parent.parent.GetComponent<UIPanel>().alpha = 1f;
    }

    void openAppraise(GameObject container, DealBody.Appraise appraise, GameObject src)
    {
        container.SetActive(true);
        CameraUtil.push(3,2);
        transform.parent.parent.GetComponent<UIPanel>().alpha = 0.1f;
        Transform tips = container.transform.FindChild("tips");
        tips.gameObject.SetActive(false);
        container.GetComponent<JustChangeLayer>().change(10);
        UIButton button = tips.FindChild("close").GetComponent<UIButton>();
        EventDelegate backEvent = new EventDelegate(this,"backFromAppraise");
        backEvent.parameters[0] = new EventDelegate.Parameter();
        backEvent.parameters[0].obj = container;
        button.onClick.Clear();
        button.onClick.Add(backEvent);
        Transform do_trans = container.transform.FindChild("do");
        do_trans.FindChild("title").GetComponent<UILabel>().text = src.name.Equals("buyer-appraise") ? "买家评价" : "卖家评价";
        tips.gameObject.SetActive(false);
        Transform edit = do_trans.FindChild("edit");
        Transform show = do_trans.FindChild("show");
        Transform level = null;
        if (appraise.isCompleted)
        {
            level = show.FindChild("level");
            edit.gameObject.SetActive(false);
            show.gameObject.SetActive(true);
            UILabel star_value = level.FindChild("value").GetComponent<UILabel>();
            if (appraise.star == 3)
            {
                star_value.text = "好评";
                star_value.color = Color.red;
            }
            else if (appraise.star == 2)
            {
                star_value.text = "中评";
                star_value.color = Color.green;
            }
            else
            {
                star_value.text = "差评";
                star_value.color = Color.black;
            }
            UILabel content = show.FindChild("content").GetComponent<UILabel>();
            content.text = appraise.detail;
            button = show.FindChild("cancle").GetComponent<UIButton>();
            button.onClick.Clear();
            button.onClick.Add(backEvent);
        }
        else
        {
            level = edit.FindChild("level");
            UIToggle good = level.FindChild("good").GetComponent<UIToggle>();
            UIToggle normal = level.FindChild("normal").GetComponent<UIToggle>();
            UIToggle bad = level.FindChild("bad").GetComponent<UIToggle>();
            EventDelegate tooleCheck = new EventDelegate(this,"checkFlag");
            tooleCheck.parameters[0] = new EventDelegate.Parameter();
            tooleCheck.parameters[0].obj = good;
            tooleCheck.parameters[1] = new EventDelegate.Parameter();
            tooleCheck.parameters[1].obj = normal;
            tooleCheck.parameters[2] = new EventDelegate.Parameter();
            tooleCheck.parameters[2].obj = bad;
            good.onChange.Clear();
            good.onChange.Add(tooleCheck);

            tooleCheck = new EventDelegate(this,"checkFlag");
            tooleCheck.parameters[0] = new EventDelegate.Parameter();
            tooleCheck.parameters[0].obj = normal;
            tooleCheck.parameters[1] = new EventDelegate.Parameter();
            tooleCheck.parameters[1].obj = good;
            tooleCheck.parameters[2] = new EventDelegate.Parameter();
            tooleCheck.parameters[2].obj = bad;
            normal.onChange.Clear();
            normal.onChange.Add(tooleCheck);

            tooleCheck = new EventDelegate(this, "checkFlag");
            tooleCheck.parameters[0] = new EventDelegate.Parameter();
            tooleCheck.parameters[0].obj = bad;
            tooleCheck.parameters[1] = new EventDelegate.Parameter();
            tooleCheck.parameters[1].obj = good;
            tooleCheck.parameters[2] = new EventDelegate.Parameter();
            tooleCheck.parameters[2].obj = normal;
            bad.onChange.Clear();
            bad.onChange.Add(tooleCheck);

            edit.gameObject.SetActive(true);
            show.gameObject.SetActive(false);
            edit.FindChild("level").FindChild("good").GetComponent<UIToggle>().value = true;
            button = edit.FindChild("sure").GetComponent<UIButton>();
            EventDelegate sureEvent = new EventDelegate(this,src.name.Equals("buyer-appraise") ? "buyerSure" : "sellerSure");
            sureEvent.parameters[0] = new EventDelegate.Parameter();
            sureEvent.parameters[0].obj = edit.gameObject;
            button.onClick.Clear();
            button.onClick.Add(sureEvent);

            button = edit.FindChild("cancle").GetComponent<UIButton>();
            button.onClick.Clear();
            button.onClick.Add(backEvent);
        }
        
    }

    public void sellerSure(GameObject container)
    {
        sure(container,false);
    }

    public void buyerSure(GameObject container)
    {
        sure(container,true);
    }

    void sure(GameObject container,bool flag)
    {
        Transform level = container.transform.FindChild("level");
        UIToggle good   = level.FindChild("good").GetComponent<UIToggle>();
        UIToggle normal = level.FindChild("normal").GetComponent<UIToggle>();
        byte type = (byte)(good.value ? 3 : (normal ? 2 : 1));
        UIInput inputFiled = container.transform.FindChild("inputer").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(inputFiled.value))
        {
            UILabel label = inputFiled.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("Appraise");
        buffer.WriteByte((byte)(flag?1:0));
        buffer.WriteLong(order.dealId);
        buffer.WriteLong(order.id);
        buffer.WriteByte(type);
        buffer.WriteString(inputFiled.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void _fk()
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealOrderUpdate");
        buffer.WriteLong(order.dealId);
        buffer.WriteLong(order.id);
        buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteByte(1);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void _fh()
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealOrderUpdate");
        buffer.WriteLong(order.dealId);
        buffer.WriteLong(order.id);
        buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteByte(2);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void _qr()
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealOrderUpdate");
        buffer.WriteLong(order.dealId);
        buffer.WriteLong(order.id);
        buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteByte(3);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void commit(GameObject owner)
    {
        if (owner.name.Equals("fk"))
        {
            ConfirmUtil.confirm("提交已付款提交后无法取消?",_fk);
        }
        else if (owner.name.Equals("fh"))
        {
            ConfirmUtil.confirm("提交已发货提交后无法取消?",_fh);
        }
        else if (owner.name.Equals("qr"))
        {
            ConfirmUtil.confirm("提交确认收货提交后无法取消?",_qr);
        }
    }

    public void checkFlag(UIToggle me,UIToggle other1,UIToggle other2)
    {
        if (me.value)
        {
            if (other1.value)
            {
                other1.value = false;
            }
            if (other2.value)
            {
                other2.value = false;
            }
        }
    }
}
