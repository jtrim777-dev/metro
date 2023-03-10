import json

body = {"parent": "metro:block/stake_crop", "textures": {"plant": ""}}

name = input("Plant name: ")

for i in range(4):
    body["textures"]["plant"] = "metro:block/vine%i" % i

    with open("%s%i.json" % (name, i), 'w') as ofile:
        json.dump(body, ofile)

for i in range(2):
    body["textures"]["plant"] = "metro:block/vine%s%i" % (name, i+4)

    with open("%s%i.json" % (name, i+4), 'w') as ofile:
        json.dump(body, ofile)
