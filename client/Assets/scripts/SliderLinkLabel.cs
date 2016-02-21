using UnityEngine;
using System.Collections;

public class SliderLinkLabel : MonoBehaviour {

    public UIInput input;

    public int min;

    public int max;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void changeToLink()
    {
        UISlider uSlider = gameObject.GetComponent<UISlider>();
        int num = (int)(uSlider.value * max);
        if (num < min){
            num = min;
            uSlider.value = System.Convert.ToSingle(num) / System.Convert.ToSingle(max);
        }
        input.value = num + "";
    }
}
