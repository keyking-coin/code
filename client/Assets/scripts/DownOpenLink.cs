using UnityEngine;
using System.Collections;

public class DownOpenLink : MonoBehaviour
{

	public GameObject next;

	bool isOpen = false;

    public float offset = 0;

    GameObject suns = null;

	// Use this for initialization
	void Start () {
        Transform suns_tran = transform.FindChild("suns");
        if (suns_tran != null)
        {
            suns = transform.FindChild("suns").gameObject;
        }
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("UserCredit");
        if (buffer != null)
        {
            init_credit(buffer);
            GameObject obj1 = transform.FindChild("down").gameObject;
            GameObject obj2 = transform.FindChild("up").gameObject;
            open(obj1,obj2);
        }
	}

    void init_credit(ByteBuffer buffer)
    {
        string curValue      = buffer.ReadString();
        string maxValue      = buffer.ReadString();
        string tempMaxValue  = buffer.ReadString();
        string totalDealVale = buffer.ReadString();
        int hp               = buffer.ReadInt();
        int zp               = buffer.ReadInt();
        int cp               = buffer.ReadInt();
        UILabel cur_value = suns.transform.FindChild("cur-value").FindChild("Label").GetComponent<UILabel>();
        cur_value.text = curValue;
        UILabel max_value = suns.transform.FindChild("max-value").FindChild("Label").GetComponent<UILabel>();
        max_value.text = maxValue;
        UILabel temp_value = suns.transform.FindChild("temp-value").FindChild("Label").GetComponent<UILabel>();
        temp_value.text = tempMaxValue;
        UILabel deal_value = suns.transform.FindChild("deal-value").FindChild("Label").GetComponent<UILabel>();
        deal_value.text = totalDealVale;
        UILabel hp_value = suns.transform.FindChild("hp").FindChild("Label").GetComponent<UILabel>();
        hp_value.text = hp + "";
        UILabel zp_value = suns.transform.FindChild("zp").FindChild("Label").GetComponent<UILabel>();
        zp_value.text = zp + "";
        UILabel cp_value = suns.transform.FindChild("cp").FindChild("Label").GetComponent<UILabel>();
        cp_value.text = cp + "";
    }

    public void closeLink()
    {
        isOpen = false;
        transform.FindChild("down").gameObject.SetActive(true);
        transform.FindChild("up").gameObject.SetActive(false);
        suns.SetActive(isOpen);
        offset = 0;
    }

    public void open(GameObject obj1 , GameObject obj2)
    {
        obj1.SetActive(false);
        obj2.SetActive(true);
		isOpen = !isOpen;
        if (offset > 0)
        {
            GameObject obj = next;
            while (obj != null)
            {
                float x = obj.transform.localPosition.x;
                float y = obj.transform.localPosition.y + (isOpen ? -1 : 1) * offset;
                obj.transform.localPosition = new Vector3(x,y,0);
                DownOpenLink link = obj.GetComponent<DownOpenLink>();
                obj = link == null ? null : link.next;
            }
        }
        if (suns != null)
        {
            suns.SetActive(isOpen);
        }
	}

    public void openCredit()
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("UserCredit");
        buffer.WriteLong(MainData.instance.user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }
}
 
 
