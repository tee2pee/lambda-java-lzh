# lambda-java-lzh

PythonでLambda関数を作成時にLZH形式の圧縮ファイルを解凍する機能が必要となり、Pythonのライブラリに良さげなものが見つからなかったのでJavaで実装。
SAMを使って実装～デプロイまで行った。せっかくなので公開。
外部Lambda呼び出しを使ってPythonから呼び出して利用している。Lambda便利。

# Usage

```python
import boto3
import json
import base64

with open(TARGET_FILE) as f:
    res = boto3.client('lambda').invoke(
        FunctionName=FUNCTION_NAME,
        InvocationType='RequestResponse',
        Payload=json.dumps({
            'src': base64.b64encode(f.read()).decode('utf-8')
        })
    )
    if res is None:
        print('error')
    else:
        result = json.loads(json.loads(res['Payload'].read()))
        if result is None:
            print('error')
        else:
            print({k:base64.b64decode(v) for k,v in result.items()})
```