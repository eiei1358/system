# -*- coding: utf-8 -*-
"""画面遷移図 v6：ラベルを独立テキスト(白背景)で空き位置に固定配置し、線と文字の重なりを解消。
   管理者側にログアウトを追加。drawioとPNGを同一データから出力。"""
from PIL import Image, ImageDraw, ImageFont

SCREEN=("swimlane;html=1;startSize=30;fillColor=#ffffff;strokeColor=#000000;"
        "fontStyle=1;fontSize=13;horizontal=1;swimlaneFillColor=#ffffff;verticalAlign=top;")
DIAMOND="rhombus;whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;"
TITLEBOX=("whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;align=left;"
          "verticalAlign=top;fontSize=13;fontStyle=1;spacingLeft=8;spacingTop=6;")
LABEL=("text;html=1;align=center;verticalAlign=middle;fillColor=#ffffff;strokeColor=none;"
       "fontSize=13;spacing=2;")

def esc(s):
    return (s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
             .replace('"',"&quot;").replace("\n","&#10;"))

class Page:
    def __init__(s,name,did,pw,ph):
        s.name=name; s.did=did; s.pw=pw; s.ph=ph
        s.nodes=[]; s.edges=[]; s.labels=[]
    def node(s,nid,label,x,y,w,h,kind=SCREEN):
        s.nodes.append((nid,label,x,y,w,h,kind)); return (nid,x,y,w,h)
    def edge(s,src,tgt,exit,entry,points=None,dashed=False,arrow=True):
        s.edges.append(dict(src=src,tgt=tgt,exit=exit,entry=entry,points=points or [],dashed=dashed,arrow=arrow))
    def label(s,text,cx,cy,w=None):
        s.labels.append((text,cx,cy,w))

def port_pt(node, frac):
    nid,label,x,y,w,h,kind=node; fx,fy=frac
    return (x+fx*w, y+fy*h)

# ---- drawio 出力 ----
def emit(page, nmap):
    c=['        <mxCell id="0" />','        <mxCell id="1" parent="0" />']
    for nid,label,x,y,w,h,kind in page.nodes:
        c.append(f'        <mxCell id="{nid}" value="{esc(label)}" style="{kind}" vertex="1" parent="1">'
                 f'<mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')
    for i,e in enumerate(page.edges):
        ex,ey=e["exit"]; nx,ny=e["entry"]
        st=("edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;fontSize=13;jettySize=20;"
            f"exitX={ex};exitY={ey};entryX={nx};entryY={ny};"
            +("endArrow=classic;" if e["arrow"] else "endArrow=none;")
            +("dashed=1;" if e["dashed"] else ""))
        pts="".join(f'<mxPoint x="{px}" y="{py}"/>' for px,py in e["points"])
        arr=f'<Array as="points">{pts}</Array>' if pts else ""
        c.append(f'        <mxCell id="{page.did}_e{i}" style="{st}" edge="1" parent="1" '
                 f'source="{e["src"]}" target="{e["tgt"]}"><mxGeometry relative="1" as="geometry">{arr}</mxGeometry></mxCell>')
    for j,(text,cx,cy,w) in enumerate(page.labels):
        w=w or (len(text)*14+16); h=22
        c.append(f'        <mxCell id="{page.did}_L{j}" value="{esc(text)}" style="{LABEL}" vertex="1" parent="1">'
                 f'<mxGeometry x="{cx-w/2:.0f}" y="{cy-h/2:.0f}" width="{w}" height="{h}" as="geometry"/></mxCell>')
    return (f'  <diagram name="{page.name}" id="{page.did}">\n'
            f'    <mxGraphModel dx="1400" dy="900" grid="1" gridSize="10" guides="1" tooltips="1" '
            f'connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="{page.pw}" pageHeight="{page.ph}" math="0" shadow="0">\n'
            f'      <root>\n'+"\n".join(c)+"\n      </root>\n    </mxGraphModel>\n  </diagram>")

# ---- PIL 検証描画 ----
FONT="/usr/share/fonts/opentype/ipafont-gothic/ipag.ttf"
fb=ImageFont.truetype(FONT,17); fl=ImageFont.truetype(FONT,16); ft=ImageFont.truetype(FONT,22)
import math
def render(page, path):
    S=2; img=Image.new("RGB",(page.pw*S,page.ph*S),"white"); d=ImageDraw.Draw(img)
    nmap={n[0]:n for n in page.nodes}; sc=lambda p:(p[0]*S,p[1]*S)
    def dseg(a,b,col,dash):
        if not dash: d.line([a,b],fill=col,width=2); return
        x0,y0=a;x1,y1=b;dist=math.hypot(x1-x0,y1-y0);n=max(1,int(dist/14))
        for k in range(n):
            if k%2==0:
                d.line([(x0+(x1-x0)*k/n,y0+(y1-y0)*k/n),(x0+(x1-x0)*(k+1)/n,y0+(y1-y0)*(k+1)/n)],fill=col,width=2)
    for e in page.edges:
        p0=port_pt(nmap[e["src"]],e["exit"]); p1=port_pt(nmap[e["tgt"]],e["entry"])
        poly=[p0]+e["points"]+[p1]; col=(110,110,110) if e["dashed"] else (0,0,0)
        for i in range(len(poly)-1): dseg(sc(poly[i]),sc(poly[i+1]),col,e["dashed"])
        if e["arrow"]:
            a=sc(poly[-2]); b=sc(poly[-1]); ang=math.atan2(b[1]-a[1],b[0]-a[0]);L=12;w=0.5
            d.polygon([b,(b[0]-L*math.cos(ang-w),b[1]-L*math.sin(ang-w)),(b[0]-L*math.cos(ang+w),b[1]-L*math.sin(ang+w))],fill=col)
    for nid,label,x,y,w,h,kind in page.nodes:
        X,Y,Wd,Ht=x*S,y*S,w*S,h*S
        if "rhombus" in kind:
            d.polygon([(X+Wd/2,Y),(X+Wd,Y+Ht/2),(X+Wd/2,Y+Ht),(X,Y+Ht/2)],outline="black",fill="white",width=2)
        elif "swimlane" in kind:
            d.rectangle([X,Y,X+Wd,Y+Ht],outline="black",fill="white",width=2)
            hd=30*S; d.line([(X,Y+hd),(X+Wd,Y+hd)],fill="black",width=2)
            tb=d.textbbox((0,0),label,font=fb); d.text((X+(Wd-(tb[2]-tb[0]))/2,Y+(hd-(tb[3]-tb[1]))/2-tb[1]),label,fill="black",font=fb)
        else:
            d.rectangle([X,Y,X+Wd,Y+Ht],outline="black",fill="white",width=2)
            d.text((X+8,Y+8),label,fill="black",font=ft)
    for text,cx,cy,w in page.labels:
        tb=d.textbbox((0,0),text,font=fl); tw=tb[2]-tb[0]; th=tb[3]-tb[1]
        X,Y=cx*S-tw/2, cy*S-th/2
        d.rectangle([X-5,Y-3,X+tw+5,Y+th+3],fill="white",outline=(180,180,180))
        d.text((X,Y-tb[1]),text,fill=(0,0,0),font=fl)
    img.save(path); print("rendered",path,img.size)

# ======================================================= 社員側
P1=Page("社員側","emp",1700,1200)
P1.node("title","フリマ社内システム　画面遷移図（社員側）",60,40,330,46,TITLEBOX)
P1.node("login","ログイン画面（log-001）",170,170,240,66)
P1.node("menu","トップ／物品一覧（item-101）",720,170,240,66)
P1.node("d1","",828,330,28,28,DIAMOND)
P1.node("post","出品・物品情報入力（item-103）",120,500,240,66)
P1.node("confirm","出品確認（item-109）",120,660,240,66)
P1.node("done","出品完了（item-108）",120,820,240,66)
P1.node("detail","物品詳細・応募交渉（item-107）",500,500,240,66)
P1.node("auction","物品詳細オークション（item-111）",500,660,240,66)
P1.node("deal","成約後・譲渡完了まで（item-110）",500,820,240,66)
P1.node("comp","譲渡完了画面",500,980,240,66)
P1.node("msg","メッセージ（item-104/105）",880,500,240,66)
P1.node("mypage","マイページ（item-106）",1240,500,240,66)

P1.edge("login","menu",(1,0.5),(0,0.5));                         P1.label("ログイン",565,188)
P1.edge("menu","login",(0.5,0),(0.5,0),[(840,120),(290,120)]);   P1.label("ログアウト",565,120)
P1.edge("menu","d1",(0.5,1),(0.5,0),arrow=False)
P1.edge("d1","post",(0.5,1),(0.5,0),[(842,440),(240,440)]);      P1.label("出品",240,470)
P1.edge("d1","detail",(0.5,1),(0.5,0),[(842,440),(620,440)]);    P1.label("検索／詳細表示",620,470)
P1.edge("d1","msg",(0.5,1),(0.5,0),[(842,440),(1000,440)]);      P1.label("メッセージ",1000,470)
P1.edge("d1","mypage",(0.5,1),(0.5,0),[(842,440),(1360,440)]);   P1.label("マイページ",1360,470)
P1.edge("post","confirm",(0.5,1),(0.5,0));                       P1.label("確認画面へ",240,613)
P1.edge("confirm","post",(1,0.5),(1,0.5),[(430,693),(430,533)],dashed=True); P1.label("修正する",430,613)
P1.edge("confirm","done",(0.5,1),(0.5,0));                       P1.label("出品する",240,773)
P1.edge("done","menu",(0,0.5),(0.5,0),[(60,853),(60,110),(840,110)],dashed=True); P1.label("トップへ",150,110)
P1.edge("detail","auction",(0.5,1),(0.5,0));                     P1.label("オークション入札",620,613)
P1.edge("detail","msg",(1,0.5),(0,0.5));                         P1.label("メッセージ／応募",810,533)
P1.edge("detail","deal",(1,0.3),(1,0.5),[(780,520),(780,853)]);  P1.label("応募成立",790,690)
P1.edge("deal","comp",(0.5,1),(0.5,0));                          P1.label("譲渡完了を登録",620,933)
P1.edge("mypage","post",(0.5,0),(0.85,0),[(1360,455),(324,455)],dashed=True); P1.label("編集／再出品",820,455)

# ======================================================= 管理者側
P2=Page("管理者側","admin",2760,1000)
P2.node("title","フリマ社内システム　画面遷移図（管理者側）",60,40,330,46,TITLEBOX)
P2.node("alogin","管理者ログイン（log-002）",740,170,240,66)
P2.node("a701","管理者メニュー／社員情報管理（701）",1240,170,260,66)
P2.node("tmppw","仮パスワード発行",1245,330,240,48)
P2.node("d2","",1346,470,28,28,DIAMOND)
adm=[("a702","パスワードリセット（702）","パスワードリセット",80),
     ("a703","カテゴリ管理（703）","カテゴリ管理",370),
     ("a704","検索キー管理（704）","検索キー管理",660),
     ("a706","NGキーワード登録（706）","NGキーワード登録",950),
     ("a709","利用制限（709）","利用制限",1240),
     ("a710","統計情報一覧（710）","統計情報",1530),
     ("a705","不適切コンテンツ監視（705）","不適切コンテンツ監視",1820),
     ("a707","物品削除（707）","物品削除",2110),
     ("a708","メッセージ削除（708）","メッセージ削除",2400)]
for nid,boxlabel,_,x in adm:
    P2.node(nid,boxlabel,x,700,240,66)

P2.edge("alogin","a701",(1,0.5),(0,0.5));                        P2.label("ログイン",1110,188)
P2.edge("a701","alogin",(0.5,0),(0.5,0),[(1370,120),(860,120)]); P2.label("ログアウト",1115,120)
P2.edge("a701","tmppw",(0.5,1),(0.5,0));                         P2.label("社員追加",1470,283)
P2.edge("tmppw","d2",(0.5,1),(0.5,0),arrow=False)
for nid,_,elabel,x in adm:
    cx=x+120
    P2.edge("d2",nid,(0.5,1),(0.5,0),[(1360,640),(cx,640)]);     P2.label(elabel,cx,668)
P2.edge("a705","a707",(0.5,1),(0.5,1),[(1940,860),(2230,860)],dashed=True); P2.label("監視→削除",2085,860)
P2.edge("a705","a708",(0.65,1),(0.5,1),[(2096,890),(2520,890)],dashed=True); P2.label("監視→削除",2300,890)

# ---- 出力 ----
nmap1={n[0]:n for n in P1.nodes}; nmap2={n[0]:n for n in P2.nodes}
parts=['<mxfile host="app.diagrams.net" type="device">', emit(P1,nmap1), emit(P2,nmap2), '</mxfile>']
xml="\n".join(parts)+"\n"
open('/home/user/system/docs/design/画面遷移図_フリマ社内システム.drawio','w',encoding='utf-8').write(xml)
print("drawio saved")
render(P1,"/home/user/system/docs/design/画面遷移図_社員側.png")
render(P2,"/home/user/system/docs/design/画面遷移図_管理者側.png")
