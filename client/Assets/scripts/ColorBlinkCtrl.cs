using UnityEngine;
using System.Collections;

public class ColorBlinkCtrl : MonoBehaviour
{

    bool blink;

    Color save;

    Color color;

    int count;

    UISprite target = null;

	// Use this for initialization
	void Start () {
        target = transform.GetComponent<UISprite>();
        save   = target.color;
	}
	
	// Update is called once per frame
	void Update () {
        if (blink == true)
        {
            if (count == 30)
            {
                target.color = Color.red;
            }
            else if (count == 40)
            {
                target.color = save;
                count = 0;
            }
            count++;
        }
	}

    public void Blink (bool flag , Color color)
    {
        blink = flag;
        this.color = color;
        if (!flag)
        {
            target.color = save;
        }
    }

    public void cancle()
    {
        blink = false;
        target.color = save;
    }
}
