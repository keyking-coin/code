using UnityEngine;
using System.Collections;

public class PageDragEvent : MonoBehaviour {

    Transform page1;

    Transform page2;

    float moveSpeed = 0;

    Transform t1;

    Transform t2;

	// Use this for initialization
	void Start () {
        page1 = transform.parent.FindChild("page1");
        page2 = transform.parent.FindChild("page2");
        t1 = transform.FindChild("1");
        t2 = transform.FindChild("2");
	}
	
	// Update is called once per frame
	void Update () {
        if (moveSpeed != 0)
        {
            
            float x1 = page1.localPosition.x;
            float x2 = page2.localPosition.x;
            float y1 = page1.localPosition.y;
            float y2 = page2.localPosition.y;
            if (x1 + moveSpeed >= -Mathf.Abs(moveSpeed) && x1 + moveSpeed <= Mathf.Abs(moveSpeed))
            {
                page1.localPosition = new Vector3(0,y1,0);
                page2.localPosition = new Vector3(800,y2,0);
                t1.transform.localPosition = new Vector3(-10, -200, 0);
                t2.transform.localPosition = new Vector3(10, -200, 0);
                moveSpeed = 0;
                return;
            }
            else if (x1 + moveSpeed >= -(800 + Mathf.Abs(moveSpeed)) && x1 + moveSpeed <= Mathf.Abs(moveSpeed) - 800)
            {
                page1.localPosition = new Vector3(-800,y1,0);
                page2.localPosition = new Vector3(0,y2,0);
                t1.transform.localPosition = new Vector3(10, -200, 0);
                t2.transform.localPosition = new Vector3(-10, -200, 0);
                moveSpeed = 0;
                return ;
            }
            page1.localPosition = new Vector3(x1+moveSpeed,y1,0);
            page2.localPosition = new Vector3(x2+moveSpeed,y2,0);
        }
	}

    void OnPress(bool pressed)
    {
        if (!pressed)
        {
            //float x1 = page1.localPosition.x;
            float x2 = page2.localPosition.x;
            if (x2 < 400)
            {
                moveSpeed = -20;
            }else{
                moveSpeed = 20;
            }
        }
    }

    void OnDrag(Vector2 delta)
    {
        if (NGUITools.GetActive(gameObject))
        {
            float x1 = page1.localPosition.x;
            float y1 = page1.localPosition.y;
            float x2 = page2.localPosition.x;
            float y2 = page2.localPosition.y;
            if (x1 + delta.x > 0 || x2 + delta.x <0)
            {
                return;
            }
            page1.localPosition = new Vector3(x1 + delta.x,y1,0);
            page2.localPosition = new Vector3(x2 + delta.x,y2,0);
        }
    }
}
