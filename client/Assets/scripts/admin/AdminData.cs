using UnityEngine;
using System.Collections.Generic;

public class AdminData  {
    public static AdminData instance = new AdminData();
    public long   id       = 0;
    public string account  = "uu_admin_001";
    public string face     = "face1";
    public string nikeName = "小游";
    public List<MainData.EmailBody> emails = new List<MainData.EmailBody>();

    public void deserialize(ByteBuffer buffer)
    {
        id = buffer.ReadLong();
        account = buffer.ReadString();
        face = buffer.ReadString();
        nikeName = buffer.ReadString();
        int size = buffer.ReadInt();
        emails.Clear();
        if (size > 0)
        {
            for (int i = 0; i < size; i++)
            {
                MainData.EmailBody email = new MainData.EmailBody(this);
                email.deserialize(buffer);
                emails.Add(email);
            }
        }
    }
}
