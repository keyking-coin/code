using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class ButtonSelectEvent : MonoBehaviour {


    UITexture bg;

    public bool flag;

    bool saveFlag = false;

    public GameObject link;

    ButtonSelectEvent father;

    ButtonSelectEvent sun1;

    ButtonSelectEvent sun2;

	// Use this for initialization
	void Start () {
        bg = transform.FindChild("bg").GetComponent<UITexture>();
        father = transform.parent.parent.GetComponent<ButtonSelectEvent>();
        Transform suns = transform.FindChild("suns");
        if (suns != null)
        {
            sun1 = suns.FindChild("mm").GetComponent<ButtonSelectEvent>();
            sun2 = suns.FindChild("cj").GetComponent<ButtonSelectEvent>();
        }
        color();
        if (father != null && !father.flag)
        {
            bg.color = Color.gray;
        }
        saveFlag = flag;
	}

	// Update is called once per frame
	void Update () {

	}

    void save()
    {
        saveFlag = flag;
        bg.color = Color.gray;
    }

    void color()
    {
        if (flag)
        {
            bg.color = Color.red;
        }
        else
        {
            bg.color = Color.gray;
        }
    }

    void back()
    {
        flag = saveFlag;
        color();
        link.SetActive(flag);
    }

    public void click1(ButtonSelectEvent target, DealEvent dealEvent)
    {
        if (flag)
        {
            return;
        }
        flag = true;
        sun1.back();
        sun2.back();
        bg.color = Color.red;
        target.flag = false;
        target.sun1.save();
        target.sun2.save();
        target.bg.color = Color.gray;
        dealEvent.updateList();
    }

    public void click2(ButtonSelectEvent target, DealEvent dealEvent)
    {
        if (flag || (father != null && !father.flag))
        {
            return;
        }
        flag = true;
        link.SetActive(true);
        bg.color = Color.red;
        target.flag = false;
        target.bg.color = Color.gray;
        target.link.SetActive(false);
        dealEvent.updateList();
    }
}
