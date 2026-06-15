# -*- coding: utf-8 -*-
"""フリマ社内システム 画面遷移図（draw.io 2ページ：社員側ハブ / 管理者側ハブ）。"""

# 色: (fill, stroke, strokeWidth, bold)
GATE  = ("#f8cecc", "#b85450", 1, 0)   # ログイン（赤）
HUB   = ("#ffe082", "#ff8f00", 3, 1)   # ハブ（item-101 / 701）（黄・太枠・太字）
BLUE  = ("#dae8fc", "#6c8ebf", 1, 0)   # 出品系（青）
GREEN = ("#d5e8d4", "#82b366", 1, 0)   # 取引・物品・メッセージ（緑）
ADMIN = ("#ffe6cc", "#d79b00", 1, 0)   # 管理者（橙）
SUB   = ("#eeeeee", "#999999", 1, 0)   # サブ/補助

def esc(s):
    return (s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
             .replace('"',"&quot;").replace("\n","&#10;"))

def build_diagram(name, did, nodes, edges):
    cells = ['        <mxCell id="0" />',
             '        <mxCell id="1" parent="0" />']
    for nid,label,x,y,w,h,role in nodes:
        if role == "TITLE":
            style = "text;html=1;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;fontStyle=1;fontSize=18;"
        elif role == "LABEL":
            style = "text;html=1;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;fontStyle=1;fontSize=13;"
        else:
            fill,stroke,sw,bold = role
            style = (f"rounded=1;whiteSpace=wrap;html=1;fillColor={fill};strokeColor={stroke};"
                     f"strokeWidth={sw};fontSize=11;arcSize=10;fontStyle={'1' if bold else '0'};")
        cells.append(
            f'        <mxCell id="{nid}" value="{esc(label)}" style="{style}" vertex="1" parent="1">\n'
            f'          <mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry" />\n'
            f'        </mxCell>')
    for i,(s,t,label,extra) in enumerate(edges):
        style = ("edgeStyle=orthogonalEdgeStyle;rounded=1;html=1;endArrow=block;"
                 "jettySize=auto;orthogonalLoop=1;fontSize=10;labelBackgroundColor=#ffffff;" + extra)
        cells.append(
            f'        <mxCell id="{did}_e{i}" value="{esc(label)}" style="{style}" edge="1" parent="1" source="{s}" target="{t}">\n'
            f'          <mxGeometry relative="1" as="geometry" />\n'
            f'        </mxCell>')
    return (f'  <diagram name="{name}" id="{did}">\n'
            f'    <mxGraphModel dx="1400" dy="900" grid="1" gridSize="10" guides="1" tooltips="1" '
            f'connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="1600" pageHeight="900" math="0" shadow="0">\n'
            f'      <root>\n' + "\n".join(cells) + "\n"
            f'      </root>\n    </mxGraphModel>\n  </diagram>')

# =========================================================
# ページ1：社員側（ハブ = item-101 トップ／物品一覧）
# =========================================================
n1 = [
 ("p1title","社員側 画面遷移図　— トップ画面(item-101)を起点に各画面へ —",40,16,900,30,"TITLE"),

 ("login","1. ログイン画面\n(log-001 / BASE-101)",40,380,180,64,GATE),
 ("top","2. トップ／物品一覧画面\n(item-101)\n★メニュー起点",300,360,220,100,HUB),

 # 出品フロー（上）
 ("post","3. 出品・物品情報入力画面\n(item-103)",620,120,210,60,BLUE),
 ("ph","① 写真登録画面",900,40,170,44,BLUE),
 ("cat","② カテゴリ設定画面",900,92,170,44,BLUE),
 ("price","③ 価格条件設定画面",900,144,170,44,BLUE),
 ("limit","④ 登録期限設定画面",900,196,170,44,BLUE),
 ("confirm","4. 出品確認画面\n(item-109)",620,250,210,60,BLUE),
 ("done","5. 出品完了画面\n(item-108)",620,340,210,60,BLUE),

 # マイページ（左下）
 ("mypage","6. 出品後マイページ画面\n(item-106)",300,520,220,64,GREEN),

 # 取引フロー（下）
 ("detail","7. 物品詳細画面(応募・交渉)\n(item-107)",620,470,220,64,GREEN),
 ("auction","物品詳細(オークション)\n(item-111)",900,440,190,56,GREEN),
 ("msg","8. メッセージ画面\n(item-104 / item-105)",900,520,190,56,GREEN),
 ("deal","9. 成約後(譲渡完了まで)画面\n(item-110)",900,610,210,56,GREEN),
 ("comp","10. 譲渡完了画面",900,700,190,50,GREEN),
]
e1 = [
 ("login","top","ログイン",""),
 ("top","login","ログアウト","dashed=1;"),
 ("login","login","認証エラー(再入力)","dashed=1;strokeColor=#b85450;fontColor=#b85450;"),

 ("top","post","出品する",""),
 ("post","ph","①",""),
 ("post","cat","②",""),
 ("post","price","③",""),
 ("post","limit","④",""),
 ("post","confirm","確認画面へ",""),
 ("confirm","post","修正する","dashed=1;"),
 ("confirm","done","出品する",""),
 ("done","top","トップへ","dashed=1;"),

 ("top","mypage","マイページ",""),
 ("mypage","post","編集／再出品","dashed=1;"),

 ("top","detail","詳細表示／検索",""),
 ("detail","auction","オークション入札",""),
 ("detail","msg","メッセージ／応募",""),
 ("detail","deal","応募成立",""),
 ("deal","comp","譲渡完了を登録",""),
]

# =========================================================
# ページ2：管理者側（ハブ = 701 管理者メニュー）
# =========================================================
n2 = [
 ("p2title","管理者側 画面遷移図　— 管理者メニュー(701)を起点に各画面へ —",40,16,900,30,"TITLE"),

 ("alogin","管理者ログイン\n(log-002)",40,420,180,64,GATE),
 ("a701","管理者メニュー／社員情報管理\n(701)\n★メニュー起点",300,400,230,110,HUB),
 ("tmppw","仮パスワード発行",300,250,180,48,SUB),

 ("a702","パスワードリセット\n(702)",680,60,200,52,ADMIN),
 ("a703","カテゴリ管理\n(703)",680,140,200,52,ADMIN),
 ("a704","検索キー管理\n(704)",680,220,200,52,ADMIN),
 ("a705","不適切コンテンツ監視\n(705)",680,300,200,52,ADMIN),
 ("a706","NGキーワード登録\n(706)",680,380,200,52,ADMIN),
 ("a707","物品削除\n(707)",680,460,200,52,ADMIN),
 ("a708","メッセージ削除\n(708)",680,540,200,52,ADMIN),
 ("a709","利用制限\n(709)",680,620,200,52,ADMIN),
 ("a710","統計情報一覧\n(710)",680,700,200,52,ADMIN),
]
e2 = [
 ("alogin","a701","ログイン",""),
 ("a701","tmppw","社員追加","dashed=1;"),
 ("a701","a702","パスワードリセット",""),
 ("a701","a703","カテゴリ管理",""),
 ("a701","a704","検索キー管理",""),
 ("a701","a705","不適切コンテンツ監視",""),
 ("a701","a706","NGキーワード登録",""),
 ("a701","a707","物品削除",""),
 ("a701","a708","メッセージ削除",""),
 ("a701","a709","利用制限",""),
 ("a701","a710","統計情報",""),
 # 監視からの削除導線（補助）
 ("a705","a707","監視→削除","dashed=1;strokeColor=#999999;fontColor=#999999;"),
 ("a705","a708","監視→削除","dashed=1;strokeColor=#999999;fontColor=#999999;"),
]

xml = ('<mxfile host="app.diagrams.net" type="device">\n'
       + build_diagram("社員側（item-101起点）","emp", n1, e1) + "\n"
       + build_diagram("管理者側（701起点）","admin", n2, e2) + "\n"
       + '</mxfile>\n')

import os
out = '/home/user/system/docs/design/画面遷移図_フリマ社内システム.drawio'
with open(out,'w',encoding='utf-8') as f:
    f.write(xml)
print('saved:', out)
print('page1 nodes/edges:', len(n1), len(e1))
print('page2 nodes/edges:', len(n2), len(e2))
