using UnityEngine;
using System.Collections;

public class CameraUtil {

    private static int[] layers = new int[] {5,9,10,11};

    private static int[] saveLayer = new int[100];

    static UICamera camera = null;

    public static void push(int corsur , int index)
    {
        if (camera == null)
        {
            camera = GameObject.Find("main-Camera").GetComponent<UICamera>();
        }
        int layer = 1 << layers[index];
        saveLayer[corsur] = camera.eventReceiverMask.value;
        camera.eventReceiverMask.value = layer;
    }

    public static void pop(int corsur)
    {
        if (camera == null)
        {
            camera = GameObject.Find("main-Camera").GetComponent<UICamera>();
        }
        camera.eventReceiverMask.value = saveLayer[corsur];
    }

    public static UICamera Camera(){
        return camera;
    }
}
 
