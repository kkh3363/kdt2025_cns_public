
const sanitizeHtml = require('sanitize-html');

const SANITIZE = {
  allowedTags: [...sanitizeHtml.defaults.allowedTags, 'img','h1','h2','span'],
  allowedAttributes: {
    ...sanitizeHtml.defaults.allowedAttributes,
    img: ['src','alt','width','height','style'],
    span: ['style'],
    p: ['style'],
    div: ['style'],
  },
  allowedSchemes: ['data','http','https'],
};

exports.createEdit = (req, res) => {
    
    console.log('--------------------', req.body );
    try {
        const formData = req.body;
        //console.log('Rev Data : ', formData);

        for (let key of formData.keys()) {
             console.log(key);     // key
        } 


        const { title = '', content = '', hashtag = '', user_id = 1 } = req.body || {};
        const t = String(title).trim();
        const c = String(content).trim();
        console.log(req.body);
        
        if (!t || !c || c === '<p><br></p>') {
        return res.status(400).json({ error: 'title, content는 필수입니다.' });
        }

        const safeHtml = sanitizeHtml(c, SANITIZE);
        const thumbnailUrl = req.file ? `/uploads/${req.file.filename}` : null;

        return res.status(201).json({ ok: true, id });
    } catch (err) {
        console.error('[POST /editor/posts] 실패', {
            msg: err.message,
            stack: err.stack,
            bodyKeys: Object.keys(req.body || {}),
            hasFile: !!req.file,
        });
        return res.status(500).json({ error: '글 저장 실패', detail: err.message });
    }
  };
