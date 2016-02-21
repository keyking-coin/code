using UnityEngine;
using System.Collections;

public class DialogUtil : MonoBehaviour {

    private static GameObject currentTip = null;

	EventDelegate needDoSmoething = null;

	void Start () {

	}

	void Update () {

	}

	public static void tip(string str , bool flag = false , EventDelegate doSomthing = null){
        if (currentTip == null)
        {
            currentTip = GameObject.Find("pops").transform.FindChild("tip").gameObject;
            currentTip.SetActive(true);
        }
        UILabel label = currentTip.transform.FindChild("label-tip").gameObject.GetComponent<UILabel>();
        label.text = str;
        if (!currentTip.activeSelf)
        {
            currentTip.SetActive(true);
        }
        UISprite sprite = currentTip.transform.FindChild("flag").gameObject.GetComponent<UISprite>();
        sprite.spriteName = flag ? "Checkmark" : "X Mark";
        Transform trans_base = currentTip.transform.parent.parent;
        for (int i = 0; i < trans_base.childCount; i++)
        {
            Transform sun = trans_base.GetChild(i);
            if (!sun.Equals(currentTip.transform.parent))
            {
                MyUtilTools.changeAlpha(0.2f, sun.gameObject);
            }
        }
        CameraUtil.push(2,3);
		if (doSomthing != null)
		{
			currentTip.GetComponent<DialogUtil>().needDoSmoething = doSomthing;
		}
	}

    public void close()
    {
        gameObject.SetActive(false);
        CameraUtil.pop(2);
        Transform trans_base = currentTip.transform.parent.parent;
        for (int i = 0; i < trans_base.childCount; i++)
        {
            Transform sun = trans_base.GetChild(i);
            if (!sun.Equals(currentTip.transform.parent))
            {
                MyUtilTools.changeAlpha(1f, sun.gameObject);
            }
        }
        currentTip = null;
		if (needDoSmoething != null)
		{
			needDoSmoething.Execute();
			needDoSmoething = null;
		}
    }

    public static bool isPopTips()
    {
        return currentTip != null;
    }

    public static void dispear()
    {
        if (currentTip != null){
            currentTip.GetComponent<DialogUtil>().close();
        }
    }
}
 
 
