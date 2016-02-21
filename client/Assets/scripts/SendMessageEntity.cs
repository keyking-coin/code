using UnityEngine;
using System.Collections.Generic;
public class SendMessageEntity : MonoBehaviour {
    public ByteBuffer buffer = ByteBuffer.Allocate(1024);
    public List<string> names = new List<string>();
}
