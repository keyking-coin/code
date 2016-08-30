var levels = [
    //0-3
    ["<option value='00'>00</option>","<option value='01'>01</option>",
    "<option value='02'>02</option>","<option value='03'>03</option>" ],
    //0-0
    ["<option value='00'>00</option>"],
    //1-5
    ["<option value='01'>01</option>","<option value='02'>02</option>",
    "<option value='03'>03</option>","<option value='04'>04</option>",
    "<option value='05'>05</option>"
    ],
    //1-10
    ["<option value='01'>01</option>","<option value='02'>02</option>",
    "<option value='03'>03</option>","<option value='04'>04</option>",
    "<option value='05'>05</option>","<option value='06'>06</option>",
    "<option value='07'>07</option>","<option value='08'>08</option>",
    "<option value='09'>09</option>","<option value='10'>10</option>"
    ],
    //1-15
    ["<option value='01'>01</option>","<option value='02'>02</option>",
    "<option value='03'>03</option>","<option value='04'>04</option>",
    "<option value='05'>05</option>","<option value='06'>06</option>",
    "<option value='07'>07</option>","<option value='08'>08</option>",
    "<option value='09'>09</option>","<option value='10'>10</option>",
    "<option value='11'>11</option>","<option value='12'>12</option>",
    "<option value='13'>13</option>","<option value='14'>14</option>",
    "<option value='15'>15</option>"
    ],
    []//0-0
];
var grounds = [
                "<option grade='1' value='none'>空</option>",
			    "<option grade='0' value='conscript'>动员兵</option>",
			    "<option grade='0' value='solider'>美国大兵</option>",
				"<option grade='1' value='teslaTrooper'>磁爆步兵</option>",
				"<option grade='0' value='flakTrooper'>防空步兵</option>",
				"<option grade='0' value='rocketeer'>火箭兵</option>",
				"<option grade='1' value='rocketFlight'>火箭飞行兵</option>",
				"<option grade='0' value='sniper'>狙击手</option>",
				"<option grade='0' value='desolator'>辐射工兵</option>",
				"<option grade='1' value='tanya'>谭雅</option>",
				"<option grade='0' value='terrorRobot'>恐怖机器人</option>",
				"<option grade='0' value='chaosDrone'>神经突击车</option>",
				"<option grade='1' value='teslaArmedCar'>磁爆装甲车</option>",
				"<option grade='0' value='flakCaterpillar'>防空履带车</option>",
				"<option grade='0' value='IFV'>多功能步兵车</option>",
				"<option grade='1' value='rocketV3'>V3火箭发射车</option>",
				"<option grade='0' value='antiTank'>反坦克战车</option>",
				"<option grade='0' value='SPG'>自行火炮车</option>",
				"<option grade='1' value='TD'>坦克歼击车</option>",
				"<option grade='0' value='rhino'>犀牛坦克</option>",
				"<option grade='0' value='mirage'>幻影坦克</option>",
				"<option grade='1' value='guardian'>守护者坦克</option>",
				"<option grade='0' value='grizzly'>灰熊坦克</option>",
				"<option grade='0' value='hammer'>铁锤坦克</option>",
				"<option grade='1' value='apocalypse'>天启坦克</option>",
				"<option grade='0' value='prism'>光棱坦克</option>",
				"<option grade='0' value='tesla'>磁能坦克</option>",
				"<option grade='1' value='tankKiller'>坦克杀手</option>",
	        ];
var planes = [
                "<option grade='1' value='none'>空</option>",
				"<option grade='0' value='nightHawk'>夜鹰直升机</option>",
				"<option grade='0' value='twinBlade'>双刃直升机</option>",
				"<option grade='1' value='freeze'>冷冻直升机</option>",
				"<option grade='0' value='intruder'>入侵者</option>",
				"<option grade='0' value='peaceKeep'>维和轰炸机</option>",
				"<option grade='1' value='kirov'>基诺夫飞艇</option>",
				"<option grade='0' value='meager'>米格战斗机</option>",
				"<option grade='0' value='apollo'>阿波罗战斗机</option>",
				"<option grade='1' value='blackHawk'>黑鹰战机</option>"
	        ];
var builds = [
				"<option grade='4' value='prismTower'>光棱塔</option>",
				"<option grade='3' value='teslaCoil'>磁爆线圈</option>",
				"<option grade='2' value='cannon'>巨炮</option>"
	        ];
var hooks = [
		        "<option grade='0' value='teslagrid'>电网</option>",
				"<option grade='0' value='HEMine'>高爆地雷</option>",
				"<option grade='0' value='AntitankMine'>反坦克地雷</option>",
				"<option grade='0' value='AntidefenseMissile'>防空导弹</option>"
	        ];