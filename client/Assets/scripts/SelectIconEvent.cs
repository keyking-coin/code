using UnityEngine;
using System.Collections;

public class SelectIconEvent : JustChangeLayer
{
    public UISprite target;

    static GameObject baseFace;

	// Use this for initialization
	void Start () {

	}

    public void init()
    {
        if (baseFace == null)
        {
            baseFace = Resources.Load<GameObject>("prefabs/face");
        }
        int count = 1;
        float startx = -340;
        float x = startx;
        float y = 425;
        for (int i = 1; i < 100; i++)
        {
            string name = "face" + i;
            GameObject child = NGUITools.AddChild(gameObject,baseFace);
            child.transform.localPosition = new Vector3(x, y, 0);
            child.name = name;
            UISprite sprite = child.transform.FindChild("Sprite").GetComponent<UISprite>();
            sprite.spriteName = name;
            UIButton button = child.GetComponent<UIButton>();
            button.tweenTarget = null;
            EventDelegate button_event = new EventDelegate(this, "select");
            button_event.parameters[0] = new EventDelegate.Parameter();
            button_event.parameters[0].obj = child;
            button.onClick.Add(button_event);
            if (target.spriteName.Equals(name))
            {
                ColorBlinkCtrl ctrl = child.transform.FindChild("Sprite").GetComponent<ColorBlinkCtrl>();
                ctrl.Blink(true, Color.red);
            }
            if (count == 9)
            {
                x = startx;
                y -= 85;
                count = 0;
            }
            else
            {
                x += 85;
            }
            count++;
        }
    }

    public void clear()
    {
        MyUtilTools.clearChild(gameObject.transform);
    }

	// Update is called once per frame
	void Update () {
        run();
	}

    void select(GameObject selectObj)
    {
        if (target.spriteName.Equals(selectObj.name))
        {
            return;
        }
        ColorBlinkCtrl preCtrl = transform.FindChild(target.spriteName).FindChild("Sprite").GetComponent<ColorBlinkCtrl>();
        preCtrl.cancle();
        selectObj.transform.FindChild("Sprite").GetComponent<ColorBlinkCtrl>().Blink(true,Color.red);
        target.spriteName = selectObj.name;
    }
}
