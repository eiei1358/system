# -*- coding: utf-8 -*-
"""フリマ社内システム 画面遷移図 v5（可読性重視・くし形分岐）。
   - 画面=ヘッダー区切り箱／分岐=ひし形／遷移=ラベル付き矢印（白背景）
   - ひし形からの分岐は参考PDFと同じ「下→横バス→各画面の上」くし形。"""

SCREEN=("swimlane;html=1;startSize=30;fillColor=#ffffff;strokeColor=#000000;"
        "fontStyle=1;fontSize=13;horizontal=1;swimlaneFillColor=#ffffff;verticalAlign=top;")
DIAMOND="rhombus;whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;"
TITLEBOX=("whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;align=left;"
          "verticalAlign=top;fontSize=13;fontStyle=1;spacingLeft=8;spacingTop=6;")

def estyle(extra=""):
    return ("edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;endArrow=classic;"
            "fontSize=13;fontColor=#000000;labelBackgroundColor=#ffffff;jettySize=20;"+extra)

def esc(s):
    return (s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
             .replace('"',"&quot;").replace("\n","&#10;"))

def build(name,did,nodes,edges,pw,ph):
    c=['        <mxCell id="0" />','        <mxCell id="1" parent="0" />']
    for nid,label,x,y,w,h,style in nodes:
        c.append(f'        <mxCell id="{nid}" value="{esc(label)}" style="{style}" vertex="1" parent="1">\n'
                 f'          <mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry" />\n        </mxCell>')
    for i,(s,t,label,extra) in enumerate(edges):
        c.append(f'        <mxCell id="{did}_e{i}" value="{esc(label)}" style="{estyle(extra)}" edge="1" parent="1" source="{s}" target="{t}">\n'
                 f'          <mxGeometry relative="1" as="geometry" />\n        </mxCell>')
    return (f'  <diagram name="{name}" id="{did}">\n'
            f'    <mxGraphModel dx="1400" dy="900" grid="1" gridSize="10" guides="1" tooltips="1" '
            f'connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="{pw}" pageHeight="{ph}" math="0" shadow="0">\n'
            f'      <root>\n'+"\n".join(c)+"\n      </root>\n    </mxGraphModel>\n  </diagram>")

W,H=240,66; DS=28
COMB="exitX=0.5;exitY=1;entryX=0.5;entryY=0;"   # ひし形下→画面上（くし形）

# ===================== 社員側 =====================
n1=[
 ("title","フリマ社内システム　画面遷移図（社員側）",60,40,330,46,TITLEBOX),
 ("login","ログイン画面（log-001）",170,170,W,H,SCREEN),
 ("menu","トップ／物品一覧（item-101）",720,170,W,H,SCREEN),
 ("d1","",828,330,DS,DS,DIAMOND),
 # 4列（くし形の枝先）。各列は縦チェーン。
 ("post","出品・物品情報入力（item-103）",120,500,W,H,SCREEN),
 ("confirm","出品確認（item-109）",120,660,W,H,SCREEN),
 ("done","出品完了（item-108）",120,820,W,H,SCREEN),
 ("detail","物品詳細・応募交渉（item-107）",500,500,W,H,SCREEN),
 ("auction","物品詳細オークション（item-111）",500,660,W,H,SCREEN),
 ("deal","成約後・譲渡完了まで（item-110）",500,820,W,H,SCREEN),
 ("comp","譲渡完了画面",500,980,W,H,SCREEN),
 ("msg","メッセージ（item-104/105）",880,500,W,H,SCREEN),
 ("mypage","マイページ（item-106）",1240,500,W,H,SCREEN),
]
e1=[
 ("login","menu","ログイン","exitX=1;exitY=0.5;entryX=0;entryY=0.5;"),
 ("menu","login","ログアウト","exitX=0.5;exitY=0;entryX=0.5;entryY=0;"),
 ("menu","d1","","endArrow=none;exitX=0.5;exitY=1;entryX=0.5;entryY=0;"),
 ("d1","post","出品",COMB),
 ("d1","detail","検索／詳細表示",COMB),
 ("d1","msg","メッセージ",COMB),
 ("d1","mypage","マイページ",COMB),
 ("post","confirm","確認画面へ","exitX=0.5;exitY=1;entryX=0.5;entryY=0;"),
 ("confirm","post","修正する","dashed=1;exitX=1;exitY=0.5;entryX=1;entryY=0.5;"),
 ("confirm","done","出品する","exitX=0.5;exitY=1;entryX=0.5;entryY=0;"),
 ("done","menu","トップへ","dashed=1;exitX=0;exitY=0.5;entryX=0.5;entryY=1;"),
 ("detail","auction","オークション入札","exitX=0.5;exitY=1;entryX=0.5;entryY=0;"),
 ("detail","msg","メッセージ／応募","exitX=1;exitY=0.5;entryX=0;entryY=0.5;"),
 ("detail","deal","応募成立","exitX=0.25;exitY=1;entryX=0.25;entryY=0;"),
 ("deal","comp","譲渡完了を登録","exitX=0.5;exitY=1;entryX=0.5;entryY=0;"),
 ("mypage","post","編集／再出品","dashed=1;exitX=0.5;exitY=1;entryX=0.75;entryY=1;"),
]

# ===================== 管理者側 =====================
# ひし形→横一列の9画面（くし形）。705/707/708を右側に隣接させ「監視→削除」を短く。
order=['a702','a703','a704','a706','a709','a710','a705','a707','a708']
labels={'a702':'パスワードリセット（702）','a703':'カテゴリ管理（703）','a704':'検索キー管理（704）',
 'a706':'NGキーワード登録（706）','a709':'利用制限（709）','a710':'統計情報一覧（710）',
 'a705':'不適切コンテンツ監視（705）','a707':'物品削除（707）','a708':'メッセージ削除（708）'}
elabel={'a702':'パスワードリセット','a703':'カテゴリ管理','a704':'検索キー管理','a706':'NGキーワード登録',
 'a709':'利用制限','a710':'統計情報','a705':'不適切コンテンツ監視','a707':'物品削除','a708':'メッセージ削除'}
x0,gap,rowy=80,290,700
row=[(nid,labels[nid],x0+i*gap,rowy,W,H,SCREEN) for i,nid in enumerate(order)]
cx=(x0 + x0+(len(order)-1)*gap + W)//2  # 行の中心
n2=[
 ("title","フリマ社内システム　画面遷移図（管理者側）",60,40,330,46,TITLEBOX),
 ("alogin","管理者ログイン（log-002）",cx-620,170,W,H,SCREEN),
 ("a701","管理者メニュー／社員情報管理（701）",cx-120,170,260,H,SCREEN),
 ("tmppw","仮パスワード発行",cx-115,330,W,48,SCREEN),
 ("d2","",cx-DS//2,470,DS,DS,DIAMOND),
]+row
e2=[
 ("alogin","a701","ログイン","exitX=1;exitY=0.5;entryX=0;entryY=0.5;"),
 ("a701","tmppw","社員追加","dashed=1;exitX=0.5;exitY=1;entryX=0.5;entryY=0;"),
 ("tmppw","d2","","endArrow=none;exitX=0.5;exitY=1;entryX=0.5;entryY=0;"),
]
for nid in order:
    e2.append(("d2",nid,elabel[nid],COMB))
e2.append(("a705","a707","監視→削除","dashed=1;exitX=1;exitY=0.5;entryX=0;entryY=0.5;"))
e2.append(("a705","a708","監視→削除","dashed=1;exitX=1;exitY=0.5;entryX=0;entryY=0.5;"))

pw2 = x0 + (len(order)-1)*gap + W + 120
parts = ['<mxfile host="app.diagrams.net" type="device">']
parts.append(build("社員側","emp",n1,e1,1700,1200))
parts.append(build("管理者側","admin",n2,e2,pw2,1000))
parts.append('</mxfile>')
xml = "\n".join(parts) + "\n"

out='/home/user/system/docs/design/画面遷移図_フリマ社内システム.drawio'
open(out,'w',encoding='utf-8').write(xml)
print('saved',out,'pw2=',pw2)
