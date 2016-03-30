using UnityEngine;
using System.Collections;

public class LoadUtil : MonoBehaviour {

    int count = 2000;

    private static GameObject currentLoad = null;

    UILabel tips;
	// Use this for initialization
	void Start () {

	}
	
	// Update is called once per frame
	void Update () {
		if (count > 0){//超时处理
			count --;
            if (count == 0){
                dispear();
                DialogUtil.tip("通讯超时，请检测网络再试");
                NetUtil.getInstance.clear();
            }
		}
	}

    void appear(string showStr)
    {
        gameObject.SetActive(true);
        count = 2000;
        if (tips == null){
            tips = transform.FindChild("tips").GetComponent<UILabel>();
        }
        tips.text = showStr;
        CameraUtil.push(0,3);
        Transform trans_base = transform.parent.parent;
        for (int i = 0; i < trans_base.childCount; i++)
        {
            Transform sun = trans_base.GetChild(i);
            if (!sun.Equals(transform.parent))
            {
                MyUtilTools.changeAlpha(0.2f,sun.gameObject);
            }
        }
    }

    void dispear()
    {
        count = 0;
        currentLoad.SetActive(false);
        CameraUtil.pop(0);
        Transform trans_base = transform.parent.parent;
        for (int i = 0; i < trans_base.childCount; i++)
        {
            Transform sun = trans_base.GetChild(i);
            if (!sun.Equals(transform.parent))
            {
                MyUtilTools.changeAlpha(1f,sun.gameObject);
            }
        }
        currentLoad = null;
    }

    public static void show(bool flag, string showStr = "通讯中，请稍候")
    {
        if (flag)
        {
            if (currentLoad == null)
            {
                currentLoad = GameObject.Find("pops").transform.FindChild("loading").gameObject;
            }
            LoadUtil util = currentLoad.GetComponent<LoadUtil>();
            util.appear(showStr);
        }
        else if (currentLoad != null)
        {
            LoadUtil util = currentLoad.GetComponent<LoadUtil>();
            util.dispear();
        }
	}

    public static bool isActivity()
    {
        if (currentLoad != null)
        {
            LoadUtil util = currentLoad.GetComponent<LoadUtil>();
            return util.count > 0;
        }
        return false;
    }
}
 
 
