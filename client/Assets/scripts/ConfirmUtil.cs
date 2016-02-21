using UnityEngine;
using System.Collections;

public class ConfirmUtil{

    private static GameObject currentConfirm = null;

    public static void confirm(string tip)
    {
        confirm(tip,new EventDelegate(TryToDispear),new EventDelegate(TryToDispear));
    }

    public static void confirm(string tip , EventDelegate.Callback cSure,EventDelegate.Callback cCancle){
        confirm(tip, new EventDelegate(cSure), new EventDelegate(cCancle));
    }

    public static void confirm(string tip, EventDelegate.Callback cSure)
    {
        confirm(tip, new EventDelegate(cSure), new EventDelegate(TryToDispear));
    }

    public static void confirm(string tip, EventDelegate sure)
    {
        confirm(tip, sure,new EventDelegate(TryToDispear));
    }

    public static void confirm(string tip, EventDelegate sure , EventDelegate cancle)
    {
        if (currentConfirm != null)
        {
            return;
        }
        currentConfirm = GameObject.Find("pops").transform.FindChild("confirm").gameObject;
        currentConfirm.SetActive(true);
        UILabel label = currentConfirm.transform.FindChild("label-con").gameObject.GetComponent<UILabel>();
        label.text = tip;
        UIButton button = currentConfirm.transform.FindChild("sure-con").gameObject.GetComponent<UIButton>();
        button.onClick.Clear();
        button.onClick.Add(sure);
        button = currentConfirm.transform.FindChild("cancle-con").gameObject.GetComponent<UIButton>();
        button.onClick.Clear();
        button.onClick.Add(cancle);
        CameraUtil.push(1,3);
        Transform trans_base = currentConfirm.transform.parent.parent;
        for (int i = 0; i < trans_base.childCount; i++)
        {
            Transform sun = trans_base.GetChild(i);
            if (!sun.Equals(currentConfirm.transform.parent))
            {
                MyUtilTools.changeAlpha(0.2f,sun.gameObject);
            }
        }
    }

	public static bool isConfirmShow(){
        return currentConfirm != null;
	}

	public static void TryToDispear(){
        if (currentConfirm != null)
        {
            currentConfirm.transform.FindChild("sure-con").gameObject.GetComponent<UIButton>().onClick.Clear();
            currentConfirm.transform.FindChild("cancle-con").gameObject.GetComponent<UIButton>().onClick.Clear();
            currentConfirm.SetActive(false);
            Transform trans_base = currentConfirm.transform.parent.parent;
            for (int i = 0; i < trans_base.childCount; i++)
            {
                Transform sun = trans_base.GetChild(i);
                if (!sun.Equals(currentConfirm.transform.parent))
                {
                    MyUtilTools.changeAlpha(1f,sun.gameObject);
                }
            }
            CameraUtil.pop(1);
            currentConfirm = null;
        }
	}


}
 
 
