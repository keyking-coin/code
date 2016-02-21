using UnityEngine;
using System.Collections;

public class StartDragEvent : MonoBehaviour {

    public bool couldTouch = true ;

    byte index = 0 ;

    Transform[] trans = new Transform[5];

    UILabel label;

	// Use this for initialization
	void Start () {
        for (int i = 0; i < trans.Length; i++ )
        {
            trans[i] = transform.FindChild("star" + i);
        }
        label = transform.FindChild("tips").GetComponent<UILabel>();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    void OnPress(bool pressed)
    {
        if (pressed && couldTouch)
        {
            Ray ray = UICamera.currentCamera.ScreenPointToRay(UICamera.currentTouch.pos);
            RaycastHit hit;
            for (byte i = 0; i < trans.Length; i++ )
            {
                Transform tran = trans[i];
                Collider collider = tran.GetComponent<Collider>();
                if (collider.Raycast(ray,out hit,1000))
                {
                    setIndex((byte)(i + 1));
                    break;
                }
            }
        }
    }

    void updateLabel()
    {
        if (index < 2)
        {
            label.text = "(差评)";
        }
        else if (index >= 2 && index < 3 )
        {
            label.text = "(中评)";
        }else{
            label.text = "(好评)";
        }
    }

    void OnDrag(Vector2 delta)
    {

    }

    public byte Index
    {
        get
        {
            return index;
        }
        set
        {
            setIndex(value);
        }
    }

    void setIndex(byte index)
    {
        for (int i = 0 ; i < trans.Length; i++)
        {
            UITexture texture = trans[i].GetComponent<UITexture>();
            if (this.index == index && index == 1 && i == 0)
            {
                if (texture.color.Equals(Color.white))
                {
                    texture.color = new Color(0.5976f, 0.5976f, 0.5976f, 1f);
                }
                else
                {
                    texture.color = Color.white;
                }
                continue;
            }
            bool flag = (this.index < index) ? (i < index) : (i < index -1);
            if (flag)
            {
                texture.color = Color.white;
            }
            else
            {
                texture.color = new Color(0.5976f,0.5976f,0.5976f,1f);
            }
        }
        this.index = index;
        updateLabel();
    }
}
