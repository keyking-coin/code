using UnityEngine;
using System.Collections;


public class RuleEvent : CenterEvent {

    /*
    static string[] rules = new string[]{
        "1、本平台认证会员实行实名制，需要先实名注册认证，个人信息不完善者不能使用发帖、抢单等功能。账号一经注册不得修改个人姓名、身份证号码。本平台用户权限分为三类：认证买家会员、认证卖家会员、浏览会员。认证买家会员可进行发布“我要买”贴子和抢单、确认卖贴、查询等功能。认证卖家会员可进行发布“我要卖”和“我要买”两类帖子、抢单、确认买卖贴、查询等功能。浏览会员只可进行信息浏览。\n",
        "2、	交易流程说明：（1）发起方为卖方：卖方发“我要卖”帖→买方确认→买方打款→卖方收款→卖方发货→买方收货→双方互评。（2）发起方为买方：买方发“我要买”帖→卖方确认→买方打款→卖方收款→卖方发货→买方收货→双方互评。(3)中介服务可以保证交易双方资金安全，选择中介服务将有专人负责跟踪全程交易。\n",
        "3、	为保障交易会员利益，控制交易风险，平台对买卖双方在途交易额度初始值限定为10万元，即未完成的交易额度一旦超过10万元，将不能再进行新的交易。若要增加额度，请缴纳保证金并联系平台管理员。\n",
        "4、	双方确认帖子后，即视同签订交易合同，任意一方不得以任何理由取消交易，除非双方协商认可取消，否则违约方将被记录违约一次。累计违约1次,禁止交易功能2个月，累计违约2次，禁止交易功能半年，超过3次将永久封号。\n",
        "5、	包入库数量未达到合同约定，卖方需退还买方货款或补足入库数量。未采取弥补措施的卖方将被取消卖方资格。\n",
        "6、	发帖应写明数量，精确到包入库的最小单位。包入库品种价格平台默认包含入库鉴评费，不包含者发帖应明示。\n",
        "7、	平台造假、售假、以次充好一经发现，将永久取消该注册号交易权限。\n",
        "8、	打款时间说明：买卖双方确认帖子后，买方需在24小时内打款，逾时未打款者视同违约。\n",
        "9、	现货交易说明：（1）卖方需在收款后24小时内将货物发给买方，逾时未发，视同卖方违约。（2）品相：现货品相不以是否100%入库作为好品的评定标准，但肉眼明显能看出的非好品不能在本平台交易。以次充好本平台将严肃处理。对于品相问题，本平台有仲裁权。\n",
        "10、本平台违规行为处罚办法汇总如下，请各会员牢记，维护平台信用环境人人有功。\n（1）包入库数量不足未弥补——取消卖方资格\n（2）造假、售假、卖非好品——取消交易资格\n（3）打款逾期——记录违约1次\n（4）发货逾期——记录违约1次\n（5）其他不按约定交易的行为——记录违约1次"
    };

    static string[] questions = new string[]{
        "一、交易问题\n",
        "1、	如何认证买家资格\n手机号注册好账号后，进入个人中心，完善个人资料，上传身份证照片，即完成买家资格认证。\n",
        "2、如何认证卖家资格\n目前卖家资格认证只对良好信誉的邮币卡现货市场邮商开放，具备资质的邮商可联系平台管理员开通认证卖家。\n",
        "3、为什么提示我的信用额度不够？\n为保障交易会员利益，控制交易风险，平台对买卖双方在途交易额度初始值限定为10万元，即未完成的交易额度一旦超过10万元，将不能再进行新的交易。\n",
        "4、如何提高信用额度？增加额度有两种方法：（1）、随着交易次数额度累计，你的信用额度将逐级提高。（2）、缴纳保证金，每1000元对应10万元额度，每次认缴1000元起，认缴请站内信联系平台管理员。\n",
        "5、	交易帖子点错，发帖信息填错被人误确认如何解决？\n请双方协商取消，否则视同违约一次。\n",
        "6、	保证金如何缴纳和退回？\n缴纳保证金请汇款至平台账户（账户）留言：保证金+注册手机号退回请发站内信至管理员：申请退还保证金（）元。一旦退回，交易额度将复原。退款到账时间12-48小时。\n",
        "7、	平台是否支持中介交易？\n本平台中介交易功能正在开发，即将上线。为应对急需中介交易的朋友，特提供法人账号进行临时中介。账号：试运行期间不收取任何手续费。\n",
        "8、	如何进行中介交易？\n双方确认帖子后，同意中介则买方将货款汇到中介账户，并发站内短信通知管理员：订单号、汇款人、汇款金额、联系电话。平台确认收款后将通知买方和卖方，完成交易后，买方和卖方可站内信通知平台放款。放款结束，交易结束，双方互评。\n",
        "二、平台使用问题\n",
        "1、如何注册\n注册账户需要在登陆页面选择“手机注册”，按提示输入手机号码，点击“发送短信”，收到验证码后填入验证码，完善各项资料。手机号码将作为永久登陆账号，密码请各位会员牢记。\n",
        "2、如何使用时间轴\n时间轴是海量的文交所最新公告信息的一个时间化展示，它能提醒用户某天某交易所申购、托管预约、托管、其他重要信息。用户可根据自己需要，在“提醒”按钮选择提醒的时间方式，跟闹钟一样设置该消息的提醒，避免错过。\n",
        "3、如何使用开户直通车\n开户直通车功能是为方便广大新人开户便利设定的一条便捷开户通道，你只需按照页面提示要求将所需要的信息填入、照片上传即可。平台管理员即有专人帮您开户，免去您不在电脑前无法自助开户的烦恼。"
    };
    */

    //public FaceInAndOutEvent in_out;


    string nextShow = null;

    bool flag = false;

	// Use this for initialization
	void Start () {

	}
	
	// Update is called once per frame
	void Update () {
	
	}

    /*
    public override void click()
    {
        base.click();
        GameObject rule     = transform.FindChild("column-rule").gameObject;
        GameObject question = transform.FindChild("column-que").gameObject;
        UILabel content     = transform.FindChild("scroll").FindChild("body").FindChild("container").FindChild("content").GetComponent<UILabel>();
        _tabChange(flag ? rule : question, flag ? question : rule, content, false);
    }

    public override void backToCenter()
    {
        if (!in_out.Flag)
        {
            base.backToCenter();
        }
    }
    void _tabChange(GameObject from, GameObject to, UILabel content , bool faceOut)
    {
        if (faceOut && in_out.Flag)
        {
            return;
        }
        string[] temp = to.name.Equals("column-que") ? questions : rules;
        nextShow = "";
        foreach (string str in temp)
        {
            nextShow += str;
        }
        if (faceOut)
        {
            UILabel label_from = from.GetComponentInChildren<UILabel>();
            UILabel label_to = to.GetComponentInChildren<UILabel>();
            label_from.color = Color.gray;
            label_to.color = Color.black;
            float des = 15;
            float x1 = from.transform.localPosition.x;
            float y1 = from.transform.localPosition.y + des;
            from.transform.localPosition = new Vector3(x1, y1, 0);
            float x2 = to.transform.localPosition.x;
            float y2 = to.transform.localPosition.y - des;
            to.transform.localPosition = new Vector3(x2, y2, 0);
            flag = to.name.Equals("column-que");
            in_out.Out();
            in_out.actionOver = new EventDelegate(this,"change");
            in_out.actionOver.parameters[0] = new EventDelegate.Parameter();
            in_out.actionOver.parameters[0].obj = content;
        }
        else
        {
            change(content);
        }
    }

    void change(UILabel content)
    {
        content.transform.parent.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        content.transform.parent.parent.localPosition = Vector3.zero;
        content.text = nextShow;
        int len = MyUtilTools.computeRow(content);
        content.height = len * (content.fontSize + content.spacingY);
        float y = 550 - content.height / 2;
        content.transform.localPosition = new Vector3(0,y,0);
    }

    public void tabChange(GameObject from , GameObject to , UILabel content)
    {
        if ((flag && to.name.Equals("column-que")) || (!flag && !to.name.Equals("column-que")))
        {
            return;
        }
        _tabChange(from,to,content,true);
    }
    */
}
