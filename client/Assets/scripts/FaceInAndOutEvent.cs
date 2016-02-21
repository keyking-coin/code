using UnityEngine;
using System.Collections;

public class FaceInAndOutEvent : MonoBehaviour
{
    bool flag;

    public float speed = 10;

    byte stuts = 0;

    public EventDelegate actionOver;

	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        if (flag)
        {
            float slingSpeed = Time.deltaTime * speed;
            float x = transform.localPosition.x;
            float y = transform.localPosition.y;
            if (stuts == 0)
            {
                if (x < 800){
                    x += slingSpeed;
                    transform.localPosition = new Vector3(x,y,0);
                }
                else
                {
                    stuts = 1;
                    if (actionOver != null)
                    {
                        actionOver.Execute();
                        actionOver = null;
                    }
                }
            }
            else
            {
                if (x > 0)
                {
                    x -= slingSpeed;
                    transform.localPosition = new Vector3(x, y, 0);
                }
                else
                {
                    stuts = 3;
                    transform.localPosition = new Vector3(0,y,0);
                    flag = false;
                }
            }
        }
	}

    public void Out()
    {
        flag = true;
        stuts = 0;
    }

    public bool Flag
    {
        get
        {
            return flag;
        }
    }
}
