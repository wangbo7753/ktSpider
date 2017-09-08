# ktSpider
1.jdbc.properties需要修改数据库连接参数
2.tfds.properties可配置TFDS数据抓取开始时间，配置格式如：2016-09-27 10:34，不配置默认由当前时间倒退5分钟开始抓取；
3.teds.properties
	 首先需要确保D盘有image/teds文件夹，没有需要先手动创建下
	 访问TEDS的网站获取最新的ASP.INT_SESSIONID并配置给COOKIE值
	 teds的数据量交小，建议配置LAST_END_TIME，格式同TFDS，不配置默认由当前时间倒推5分钟
4.tpds.properties
	 ★首先需要配置COOKIE，值同teds的值，可通用；
	 ★然后很重要一点是需要访问下TPDS的网站，使用fireDebug查看页面元素或POST参数，找到__VIEWSTATE项，并赋予最新值
	 可手动配置下开始抓取时间：
	 dtTimeRange$ctl16=2016-09-27
	 dtTimeRange$ctl18=10
	 dtTimeRange$ctl22=45
	 表示程序从2016-09-27 10:45开始到当前时间抓取tpds的数据，不配置默认由当前时间倒退5分钟抓取

执行程序进入此文件夹放置的目录运行java -jar ktSpider-1.0-SNAPSHOT-jar-with-dependencies.jar即可运行
