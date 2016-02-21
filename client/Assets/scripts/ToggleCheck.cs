using UnityEngine;
using System.Collections;

public class ToggleCheck : MonoBehaviour {

    GameObject obj_true;

    GameObject obj_false;

    GameObject obj_ball;

    UIToggle toggle;

    public float left;

    public float right;

    float targetPos;

	void Start () 
    {
        obj_true  = transform.FindChild("true").gameObject;
        obj_false = transform.FindChild("false").gameObject;
        obj_ball  = transform.FindChild("ball").gameObject;
        toggle = transform.GetComponent<UIToggle>();
        targetPos = toggle.value ? right : left;
	}
	
	void Update () 
    {
        if (toggle.value && !obj_true.activeSelf)
        {
            obj_true.SetActive(true);
            targetPos = right;
        }
        if (!toggle.value && obj_true.activeSelf)
        {
            obj_true.SetActive(false);
        }
        if (!toggle.value && !obj_false.activeSelf)
        {
            obj_false.SetActive(true);
            targetPos = left;
        }
        if (toggle.value && obj_false.activeSelf)
        {
            obj_false.SetActive(false);
        }
        float x = obj_ball.transform.localPosition.x;
        if (x < targetPos)
        {
            obj_ball.transform.localPosition = new Vector3(x+5,0,0);
        }
        else if (x > targetPos)
        {
            obj_ball.transform.localPosition = new Vector3(x-5,0,0);
        }
	}
}
