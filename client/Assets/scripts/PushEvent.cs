using UnityEngine;
//using System.Collections;
using System.Collections.Generic;

public class PushEvent : MonoBehaviour
{

	public static List<DealBody> pushs = new List<DealBody>();

    int index = 0;

    GameObject lookContainer;

    GameObject base_obj;

    bool isOpen;

    public List<GameObject> others = new List<GameObject>();

	// Use this for initialization
	void Start () {
        base_obj = transform.FindChild("base").gameObject;
	}
	
	// Update is called once per frame
	void Update () {
        if (pushs.Count == 0)
        {
            return;
        }
        if (!ConfirmUtil.isConfirmShow() && !LoadUtil.isActivity() && !DialogUtil.isPopTips())
        {
            if (!isOpen)
            {
                tryToOpenPush();
                
            }
        }
        ByteBuffer buffer = MyUtilTools.tryToLogic("DealGrab");
        if (buffer != null)
        {
            DialogUtil.tip("抢单成功",true);
        }
	}

    void tryToOpenPush()
    {
        isOpen = true;
        CameraUtil.push(6,1);
        foreach (GameObject obj in others)
        {
            MyUtilTools.changeAlpha(0.2f, obj);
        }
        transform.FindChild("base").gameObject.SetActive(true);
        refresh();
    }

    void tryToClosePush()
    {
        CameraUtil.pop(6);
        foreach (GameObject obj in others)
        {
            MyUtilTools.changeAlpha(1,obj);
        }
        transform.FindChild("base").gameObject.SetActive(false);
        isOpen = false;
    }

    void _refresh()
    {
        DealBody item = pushs[index];
        Transform content = transform.FindChild("base").FindChild("content");
        Transform arrows = content.parent.FindChild("arrows");
        arrows.FindChild("left").gameObject.SetActive(index > 0);
        arrows.FindChild("right").gameObject.SetActive(index < pushs.Count - 1);
    }

    void refresh()
    {
        DealBody item = pushs[index];
        UILabel title = transform.FindChild("base").FindChild("bg").FindChild("up").GetComponentInChildren<UILabel>();
        title.text = item.seller ? "出 售" : "求 购";
        Transform content = transform.FindChild("base").FindChild("content");
        UILabel label = content.FindChild("seller").FindChild("value").GetComponent<UILabel>();
        label.text = item.userName;
        label = content.FindChild("type").FindChild("value").GetComponent<UILabel>();
        label.text = item.typeStr;
        label = content.FindChild("bourse").FindChild("value").GetComponent<UILabel>();
        string[] ss = item.bourse.Split(new char[] { ',' });
        label = content.FindChild("bourse").GetComponent<UILabel>();
        label.text = (item.typeStr.Equals("入库") ? "文 交 所 ：" : "交易城市：");
        label = label.transform.FindChild("value").GetComponent<UILabel>();
        label.text = ss[1];
        label = content.FindChild("name").FindChild("value").GetComponent<UILabel>();
        label.text = item.stampName;
        label = content.FindChild("num").FindChild("value").GetComponent<UILabel>();
        label.text = item.curNum + "(" + item.monad + ")";
        label = content.FindChild("price").FindChild("value").GetComponent<UILabel>();
        label.text = item.price + "";
        label = content.FindChild("time").FindChild("value").GetComponent<UILabel>();
        label.text = item.time;
        _refresh();
    }

    public void grab()
    {
        DealBody item = pushs[index];
        int min = 1;
        int max = item.curNum;
        transform.FindChild("base").gameObject.SetActive(false);
        GameObject grab = transform.FindChild("grab").gameObject;
        grab.SetActive(true);
        GameObject num_input = grab.transform.FindChild("inputer").gameObject;
        num_input.GetComponent<GrabInputEvent>().init(min, max);
        GameObject flag_obj = grab.transform.FindChild("flag").gameObject;
        flag_obj.SetActive(item.helpFlag);
        num_input.transform.localPosition = new Vector3(0,item.helpFlag?40:0,0);
        if (item.helpFlag)
        {
            UIButton button = flag_obj.GetComponent<UIButton>();
            button.onClick.Clear();
            UIToggle toggle = flag_obj.GetComponent<UIToggle>();
            if (!item.seller)
            {//买家选择了中介服务，卖家必须选择中介服务
                toggle.value = true;
                toggle.enabled = false;
                button.onClick.Add(new EventDelegate(item.showMustUseHelpTip));
            }
            else
            {
                toggle.enabled = true;
            }
        }
    }

    void initLook(DealBody item)
    {
        transform.FindChild("base").gameObject.SetActive(false);
        GameObject look = transform.FindChild("look").gameObject;
        look.SetActive(true);
        if (lookContainer == null)
        {
            lookContainer = look.transform.FindChild("scroll").FindChild("body").FindChild("container").gameObject;
        }
        MyUtilTools.clearChild(lookContainer.transform);
        lookContainer.transform.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        lookContainer.transform.parent.localPosition = new Vector3(0,0,0);
        if (DealEvent.pref_detail == null)
        {
            DealEvent.pref_detail = Resources.Load<GameObject>("prefabs/deal-detail");
        }
        float start = 310;
        GameObject obj_item = NGUITools.AddChild(lookContainer,DealEvent.pref_detail);
        obj_item.name = "item";
        obj_item.transform.localPosition = new Vector3(-50,start,0);
        float desc = 50;
        desc += item.insterItem(obj_item);
        GameObject obj_suns = obj_item.transform.FindChild("suns").gameObject;
        obj_suns.transform.localPosition = new Vector3(0.0f,-desc,0.0f);
        float sun_desc = 0;
        for (int i = 0; i < item.reverts.Count; i++)
        {
            DealBody.Revert revert = item.reverts[i];
            GameObject obj_sun_item = NGUITools.AddChild(obj_suns,DealEvent.pref_detail);
            obj_sun_item.name = "sun" + i;
            obj_sun_item.transform.localPosition = new Vector3(0,-sun_desc,0);
            revert.update(obj_sun_item);
            sun_desc += 50;
            if (revert.context != null)
            {
                GameObject obj_context = obj_sun_item.transform.FindChild("context").gameObject;
                UILabel label = obj_context.GetComponents<UILabel>()[0];
                int len = MyUtilTools.computeRow(label);
                label.height = len * label.fontSize;
                float a = len * label.fontSize / 2 + 30;
                obj_context.transform.localPosition = new Vector3(100, -a, 0);
                sun_desc += len * label.fontSize + 10;
            }
        }
    }

    public void look()
    {
        initLook(pushs[index]);
    }

    public void ignore()
    {
        pushs.RemoveAt(index);
        if (pushs.Count == 0)
        {
            tryToClosePush();
        }
        else
        {
            if (index == pushs.Count)
            {
                index--;
            }
            refresh();
        }
    }

	public void ignoreAll()
	{
		if (pushs.Count > 0)
		{
			index = 0;
			pushs.Clear ();
			tryToClosePush();
		}
	}

    public void left()
    {
        if (index > 0)
        {
            index--;
            refresh();
        }
    }

    public void right()
    {
        if (index < pushs.Count - 1)
        {
            index ++;
            refresh();
        }
    }

    public void grabCancle()
    {
        transform.FindChild("base").gameObject.SetActive(true);
        transform.FindChild("grab").gameObject.SetActive(false);
    }

    public void lookCancle()
    {
        transform.FindChild("base").gameObject.SetActive(true);
        transform.FindChild("look").gameObject.SetActive(false);
    }

    void confirmGrab()
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealGrab");
        buffer.WriteLong(pushs[index].id);
        buffer.WriteLong(MainData.instance.user.id);
        Transform gab_tran = transform.FindChild("grab");
        UIInput input = gab_tran.FindChild("inputer").GetComponent<UIInput>();
        int num = int.Parse(input.value);
        buffer.WriteInt(num);
        UIToggle toggle = gab_tran.FindChild("flag").GetComponent<UIToggle>();
        buffer.WriteByte((byte)(pushs[index].helpFlag && toggle.value ? 1 : 0));
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void doGrab()
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            return;
        }
        ConfirmUtil.confirm("确定抢单？",confirmGrab);
    }
}
