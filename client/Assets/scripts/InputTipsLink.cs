using UnityEngine;
using System.Collections;

public class InputTipsLink : MonoBehaviour {

    UILabel target = null;

    public bool flag;

    string allStr;

	// Use this for initialization
	void Start () {
        target = transform.parent.FindChild("show").GetComponent<UILabel>();
	}
	
	// Update is called once per frame
	void Update () {
        if (target != null)
        {
            if (gameObject.activeSelf && !MyUtilTools.stringIsNull(target.text))
            {
                gameObject.SetActive(false);
            }
        }
	}

    public void checkShow()
    {
        if (target == null)
        {
            target = transform.parent.FindChild("show").GetComponent<UILabel>();
        }
        if (target != null && MyUtilTools.stringIsNull(target.text))
        {
            gameObject.SetActive(true);
        }
    }

    public void check()
    {
        if (target == null)
        {
            target = transform.parent.FindChild("show").GetComponent<UILabel>();
        }
        allStr = target.text;
        if (flag)
        {
            MyUtilTools.insertStr(target,allStr,target.width/2);
            transform.parent.GetComponent<UIInput>().value = target.text;
        }
    }

    public string AllStr
    {
        get
        {
            return allStr;
        }
        set
        {
            allStr = value;
        }
    }
}
