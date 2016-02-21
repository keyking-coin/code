using UnityEngine;
using System.Collections;

public class SubPanelPosition : MonoBehaviour {

    void Start()
    {
        SetPanel();
    }

    public void SetPanel()
    {
        Transform parent = transform.parent;
        Transform child = transform.GetChild(0);
        UIPanel panel = transform.GetComponent<UIPanel>();
        UIScrollView scroll = transform.GetComponent<UIScrollView>();
        transform.parent = null;
        child.parent = null;
        float ScaleSize = 1;
        if (scroll.movement == UIScrollView.Movement.Vertical)
        {
            ScaleSize = transform.localScale.y;
        }
        else if (scroll.movement == UIScrollView.Movement.Horizontal)
        {
            ScaleSize = transform.localScale.x;
        }
        transform.localScale = new Vector4(ScaleSize,ScaleSize,ScaleSize,ScaleSize);
        transform.parent = parent;
        child.parent = transform;
        float scaleX = child.transform.localScale.x;
        float scaleY = child.transform.localScale.y;
        panel.baseClipRegion = new Vector4(panel.baseClipRegion.x,panel.baseClipRegion.y,panel.baseClipRegion.z * scaleX,panel.baseClipRegion.w * scaleY);
    }

    void clear()
    {
        transform.localScale = new Vector4(1,1,1,1);
        transform.GetChild(0).localScale = new Vector4(1,1,1,1);
    }
}
