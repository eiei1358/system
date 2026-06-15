# -*- coding: utf-8 -*-
"""フリマ社内システム 画面遷移図を「日記システム」サンプルと同じUML風記法で生成。
   画面=ヘッダー区切り線付きの箱(swimlane)、分岐/合流=ひし形(◇)、遷移=曲線ラベル付き矢印。"""

SCREEN = ("swimlane;html=1;startSize=28;fillColor=#ffffff;strokeColor=#000000;"
          "fontStyle=1;fontSize=12;horizontal=1;swimlaneFillColor=#ffffff;verticalAlign=top;")
DIAMOND = "rhombus;whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;"
TITLEBOX = "whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;align=left;verticalAlign=top;fontSize=12;spacingLeft=6;spacingTop=4;"

def esc(s):
    return (s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
             .replace('"',"&quot;").replace("\n","&#10;"))

def build_diagram(name, did, nodes, edges):
    cells = ['        <mxCell id="0" />','        <mxCell id="1" parent="0" />']
    for nid,label,x,y,w,h,style in nodes:
        cells.append(
            f'        <mxCell id="{nid}" value="{esc(label)}" style="{style}" vertex="1" parent="1">\n'
            f'          <mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry" />\n'
            f'        </mxCell>')
    for i,(s,t,label,extra) in enumerate(edges):
        style = ("edgeStyle=orthogonalEdgeStyle;curved=1;rounded=0;html=1;"
                 "endArrow=classic;fontSize=11;labelBackgroundColor=none;" + extra)
        cells.append(
            f'        <mxCell id="{did}_e{i}" value="{esc(label)}" style="{style}" edge="1" parent="1" source="{s}" target="{t}">\n'
            f'          <mxGeometry relative="1" as="geometry" />\n'
            f'        </mxCell>')
    return (f'  <diagram name="{name}" id="{did}">\n'
            f'    <mxGraphModel dx="1400" dy="900" grid="1" gridSize="10" guides="1" tooltips="1" '
            f'connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="1600" pageHeight="1100" math="0" shadow="0">\n'
            f'      <root>\n' + "\n".join(cells) + "\n"
            f'      </root>\n    </mxGraphModel>\n  </diagram>')

W,H = 210,64          # 画面ボックス
DS = 26               # ひし形サイズ

# =========================================================
# ページ1：社員側
# =========================================================
n1 = [
 ("title","フリマ社内システム\n画面遷移図（社員側）",60,40,220,56,TITLEBOX),

 ("login","ログイン画面（log-001）",120,150,W,H,SCREEN),
 ("menu","トップ／物品一覧（item-101）",640,150,W,H,SCREEN),
 ("d1","",733,300,DS,DS,DIAMOND),       # 分岐

 ("post","出品・物品情報入力（item-103）",120,430,W,H,SCREEN),
 ("confirm","出品確認（item-109）",120,560,W,H,SCREEN),
 ("done","出品完了（item-108）",120,690,W,H,SCREEN),

 ("detail","物品詳細・応募交渉（item-107）",430,430,W,H,SCREEN),
 ("auction","物品詳細オークション（item-111）",430,560,W,H,SCREEN),
 ("deal","成約後・譲渡完了まで（item-110）",430,690,W,H,SCREEN),
 ("comp","譲渡完了画面",430,820,W,H,SCREEN),

 ("msg","メッセージ（item-104/105）",760,430,W,H,SCREEN),
 ("mypage","マイページ（item-106）",1060,430,W,H,SCREEN),
]
e1 = [
 ("login","menu","ログイン",""),
 ("menu","login","ログアウト",""),
 ("menu","d1","","endArrow=none;"),
 ("d1","post","出品",""),
 ("d1","detail","検索／詳細表示",""),
 ("d1","msg","メッセージ",""),
 ("d1","mypage","マイページ",""),

 ("post","confirm","確認画面へ",""),
 ("confirm","post","修正する","dashed=1;"),
 ("confirm","done","出品する",""),
 ("done","menu","トップへ","dashed=1;"),

 ("detail","auction","オークション入札",""),
 ("detail","msg","メッセージ／応募",""),
 ("detail","deal","応募成立",""),
 ("deal","comp","譲渡完了を登録",""),
 ("mypage","post","編集／再出品","dashed=1;"),
]

# =========================================================
# ページ2：管理者側
# =========================================================
n2 = [
 ("title","フリマ社内システム\n画面遷移図（管理者側）",60,40,220,56,TITLEBOX),

 ("alogin","管理者ログイン（log-002）",120,160,W,H,SCREEN),
 ("a701","管理者メニュー／社員情報管理（701）",600,160,240,H,SCREEN),
 ("tmppw","仮パスワード発行",600,300,W,48,SCREEN),
 ("d2","",712,400,DS,DS,DIAMOND),

 ("a702","パスワードリセット（702）",120,540,W,H,SCREEN),
 ("a703","カテゴリ管理（703）",350,540,W,H,SCREEN),
 ("a704","検索キー管理（704）",580,540,W,H,SCREEN),
 ("a705","不適切コンテンツ監視（705）",810,540,W,H,SCREEN),
 ("a706","NGキーワード登録（706）",1040,540,W,H,SCREEN),

 ("a707","物品削除（707）",580,720,W,H,SCREEN),
 ("a708","メッセージ削除（708）",810,720,W,H,SCREEN),
 ("a709","利用制限（709）",350,720,W,H,SCREEN),
 ("a710","統計情報一覧（710）",1040,720,W,H,SCREEN),
]
e2 = [
 ("alogin","a701","ログイン",""),
 ("a701","tmppw","社員追加","dashed=1;"),
 ("a701","d2","","endArrow=none;"),
 ("d2","a702","パスワードリセット",""),
 ("d2","a703","カテゴリ管理",""),
 ("d2","a704","検索キー管理",""),
 ("d2","a705","不適切コンテンツ監視",""),
 ("d2","a706","NGキーワード登録",""),
 ("d2","a709","利用制限",""),
 ("d2","a710","統計情報",""),
 ("a705","a707","監視→削除","dashed=1;"),
 ("a705","a708","監視→削除","dashed=1;"),
]

xml = ('<mxfile host="app.diagrams.net" type="device">\n'
       + build_diagram("社員側","emp", n1, e1) + "\n"
       + build_diagram("管理者側","admin", n2, e2) + "\n"
       + '</mxfile>\n')

out = '/home/user/system/docs/design/画面遷移図_フリマ社内システム.drawio'
with open(out,'w',encoding='utf-8') as f:
    f.write(xml)
print('saved:', out)
print('p1:', len(n1),'nodes', len(e1),'edges  p2:', len(n2),'nodes', len(e2),'edges')
