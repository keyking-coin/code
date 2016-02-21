using UnityEngine;
using System.Collections;

public class GrabInputEvent : MonoBehaviour {

    int baseNum = 10;

    int maxNUm = 0;

    UIInput input = null;

    int saveNum = 10;

	// Use this for initialization
	void Start () {
        input = transform.GetComponent<UIInput>();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void init(int min , int max)
    {
        saveNum = baseNum = min;
        maxNUm = max;
        if (input == null)
        {
            input = transform.GetComponent<UIInput>();
        }
        input.value = min + "";
    }

    public void check()
    {
        if (input != null)
        {
            int num = int.Parse(input.value);
            if (num % baseNum != 0)
            {
                DialogUtil.tip("输入错误必须是" + baseNum + "的倍数");
                input.value = saveNum + "";
                return;
            }
            if (num < baseNum )
            {
                DialogUtil.tip("输入数量小于" + baseNum + "了");
                input.value = saveNum + "";
                return;
            }
            saveNum = num;
        }
    }
}
