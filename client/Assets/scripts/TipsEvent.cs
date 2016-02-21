using UnityEngine;
using System.Collections;

public class TipsEvent : MonoBehaviour {
    bool isPressed = false;
    float pressTimer = 0;
    float _longClickduration = 1;

    public Vector2 targetPos;

    GameObject tip_obj = null;

    int showCount = 0;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        if (isPressed)
        {
            pressTimer += Time.deltaTime;
            if (pressTimer >= _longClickduration)
            {
                doLongPress();
            }
        }
        else
        {
            if (tip_obj != null){
                if (showCount > 0)
                {
                    showCount--;
                }
                {
                    Destroy(tip_obj);
                    tip_obj = null;
                }
            }
        }
	}

    void OnPress(bool pressed)
    {
        isPressed = pressed;
        if (!pressed)
        {
            pressTimer = 0;
        }
    }

    void doLongPress()
    {
        if (tip_obj != null)
        {
            return;
        }
        showCount = 500;
    }
}
