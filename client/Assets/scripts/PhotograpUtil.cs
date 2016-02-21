using UnityEngine;
using System.Collections;

public class PhotograpUtil : MonoBehaviour
{

    UITexture target = null;

	// Use this for initialization
	void Start ()
    {
        UISprite rect = transform.parent.GetComponent<UISprite>();
        target = transform.parent.FindChild("context").GetComponent<UITexture>();
        target.width  = rect.width;
        target.height = rect.height;
	}
	
	// Update is called once per frame
	void Update () 
    {
        if (gameObject.activeSelf && target.mainTexture == null)
        {
            gameObject.SetActive(false);
        }
	}

    public void clear()
    {
        if (target != null)
        {
            target.mainTexture = null;
        }
    }

    public void open(PhotographEvent pEvent)
    {
        if (target.mainTexture != null)
        {
            return;
        }
        pEvent.open(new EventDelegate(this,"backFromPhotographer"));
    }

    void backFromPhotographer(Texture2D t2d)
    {
        target.mainTexture = t2d;
        gameObject.SetActive(true);
    }
}
