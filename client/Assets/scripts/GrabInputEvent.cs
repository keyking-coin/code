using UnityEngine;
using System.Collections;

public class GrabInputEvent : MonoBehaviour {

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

    public void init(int max)
    {
        saveNum = 1;
        maxNUm = max;
        if (input == null)
        {
            input = transform.GetComponent<UIInput>();
        }
        input.value = "1";
    }

    public void check()
    {
        if (input != null)
        {
            int num = int.Parse(input.value);
            if (num < maxNUm)
            {
                DialogUtil.tip("剩余数量不足" + maxNUm);
                input.value = saveNum + "";
                return;
            }
            saveNum = num;
        }
    }
}
