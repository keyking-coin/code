using UnityEngine;
using System.Collections.Generic;

public class AdminUserRZ : MonoBehaviour {

    static List<MainData.UserData> datas = new List<MainData.UserData>();

    int selectIndex = 0;
    Transform leftContainer = null;
	// Use this for initialization
	void Start () {
        if (MemberManager.pref_user_info == null)
        {
            MemberManager.pref_user_info = Resources.Load<GameObject>("prefabs/user-info");
        }
        leftContainer = transform.FindChild("left").FindChild("list").FindChild("body").FindChild("container");
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AdminSellerOpration");
        if (buffer != null)
        {
            if (selectIndex > 0)
            {
                selectIndex--;
            }
            DialogUtil.tip("操作成功", true);
            return;
        }
        if (datas.Count != leftContainer.childCount)
        {
            refresh();
        }
	}

    public static void deserializeAll(ByteBuffer data)
    {
        int size = data.ReadInt();
        for (int i = 0; i < size; i++)
        {
            MainData.UserData user = new MainData.UserData();
            user.deserialize(data);
            datas.Add(user);
        }
    }

    public static void deserializeModuleOne(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        MainData.UserData user = new MainData.UserData();
        user.deserialize(data);
        if (flag == JustRun.ADD_FLAG)
        {
            datas.Add(user);
        }
        else if (flag == JustRun.DEL_FLAG)
        {
            for (int i = 0; i < datas.Count; i++)
            {
                MainData.UserData _user = datas[i];
                if (user.id == _user.id)
                {
                    datas.Remove(_user);
                    break;
                }
            }
        }
    }

    void refresh()
    {
        leftContainer.parent.localPosition = Vector3.zero;
        leftContainer.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        MyUtilTools.clearChild(leftContainer);
        float y = 800f;
        for (int i = 0; i < datas.Count; i++)
        {
            MainData.UserData user = datas[i];
            GameObject user_summary = NGUITools.AddChild(leftContainer.gameObject,MemberManager.pref_user_info);
            user_summary.transform.localPosition = new Vector3(-180,y,0);
            user_summary.name = "" + i;
            user_summary.transform.FindChild("icon").GetComponent<UISprite>().spriteName = user.face;
            user_summary.transform.FindChild("name").GetComponent<UILabel>().text = user.nikeName;
            Transform select_trans = user_summary.transform.FindChild("select");
            select_trans.FindChild("show").gameObject.SetActive(i == selectIndex);
            UIButton button = select_trans.GetComponent<UIButton>();
            EventDelegate event_delegate = new EventDelegate(this, "select");
            event_delegate.parameters[0] = new EventDelegate.Parameter();
            event_delegate.parameters[0].obj = user_summary;
            button.onClick.Add(event_delegate);
            y -= 80;
        }
        refreshRight();
    }

    void select(GameObject obj)
    {
        int index = int.Parse(obj.name);
        if (selectIndex == index)
        {
            return;
        }
        Transform container = obj.transform.parent;
        Transform preTrans = container.FindChild(selectIndex + "");
        if (preTrans != null)
        {
            preTrans.FindChild("select").FindChild("show").gameObject.SetActive(false);
        }
        obj.transform.FindChild("select").FindChild("show").gameObject.SetActive(true);
        selectIndex = index;
        refreshRight();
    }

    void refreshRight()
    {
        Transform container = transform.FindChild("right");
        if (datas.Count == 0)
        {
            container.gameObject.SetActive(false);
            return;
        }
        container.gameObject.SetActive(true);
        MainData.UserData user = datas[selectIndex];
        container.FindChild("account").FindChild("value").GetComponent<UILabel>().text = user.account;
        container.FindChild("nickName").FindChild("value").GetComponent<UILabel>().text = user.nikeName;
        container.FindChild("name").FindChild("value").GetComponent<UILabel>().text = user.realyName;
        container.FindChild("ident").FindChild("value").GetComponent<UILabel>().text = user.indentity;
        container.FindChild("type").FindChild("value").GetComponent<UILabel>().text = user.permission == 1 ? "买家" : "卖家";
        container.FindChild("title").FindChild("value").GetComponent<UILabel>().text = user.title;
        container.FindChild("deposit").FindChild("value").GetComponent<UILabel>().text = user.deposit + "";
        container.FindChild("deal").FindChild("value").GetComponent<UILabel>().text = user.credit.totalDealValue + "";
        container.FindChild("credit-c").FindChild("value").GetComponent<UILabel>().text = user.credit.maxValue + "";
        container.FindChild("credit-t").FindChild("value").GetComponent<UILabel>().text = user.credit.tempMaxValue + "";
        container.FindChild("hp").FindChild("value").GetComponent<UILabel>().text = user.credit.hp + "";
        container.FindChild("zp").FindChild("value").GetComponent<UILabel>().text = user.credit.zp + "";
        container.FindChild("cp").FindChild("value").GetComponent<UILabel>().text = user.credit.cp + "";
        container.FindChild("regist").FindChild("value").GetComponent<UILabel>().text = user.registTime;
        container.FindChild("time").FindChild("value").GetComponent<UILabel>().text = user.endTime;
        container.FindChild("wg").FindChild("value").GetComponent<UILabel>().text = user.breach + "";
        Transform fh_body = container.FindChild("fh").FindChild("body");
        UILabel reason = fh_body.FindChild("value").GetComponent<UILabel>();
        if (user.forbid.endTime.Equals("forever"))
        {
            reason.text = "永久封号";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = "永久";
        }
        else if (user.forbid.endTime.Equals("null"))
        {
            reason.text = "未被封号";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = "无";
        }
        else
        {
            reason.text = user.forbid.reason + "";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = user.forbid.endTime;
        }
        //认证信息
        Transform rz = container.FindChild("rz-body");
        rz.FindChild("type").FindChild("value").GetComponent<UILabel>().text = user.seller.type == 0 ? "个人" : "公司";
        rz.FindChild("key").FindChild("value").GetComponent<UILabel>().text = user.seller.key;
        JustRun.Instance.loadPic(user.seller.picName,rz.FindChild("pic").FindChild("context").GetComponent<UITexture>());
    }

    void _pass()
    {
        ConfirmUtil.TryToDispear();
        MainData.UserData user = datas[selectIndex];
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminSellerOpration");
        buffer.WriteInt(0);
        buffer.WriteLong(user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void pass()
    {
        MainData.UserData user = datas[selectIndex];
        ConfirmUtil.confirm("确定通过此用户的卖家认证?",_pass);
    }

    void _reject()
    {
        ConfirmUtil.TryToDispear();
        MainData.UserData user = datas[selectIndex];
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminSellerOpration");
        buffer.WriteInt(1);
        buffer.WriteLong(user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void reject()
    {
        ConfirmUtil.confirm("确定拒绝此用户的卖家认证?", _reject);
    }
}
