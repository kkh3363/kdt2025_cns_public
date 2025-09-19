const express = require('express');
const multer  = require('multer');
const path = require('path');

// 저장 경로 및 파일 이름 지정
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'public/uploads/'); // 이 경로가 실제로 존재해야 함
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    const ext = path.extname(file.originalname);
    cb(null, file.fieldname + '-' + uniqueSuffix + ext);
  }
});

const upload = multer({ storage });

const app = express();
const port = 3000;

app.set("view engine", "ejs");
app.set("views", "./views");

app.use(express.json()); // JSON 파싱 미들웨어 추가
app.use(express.urlencoded({ extended: true }));

app.use('/uploads', express.static(path.join(__dirname, 'public/uploads')));

app.get('/', (req, res) => {
  res.render('saveform');
});

app.post('/save', (req, res) => {
  const { content } = req.body;
  // content를 DB에 저장하거나 파일로 저장 가능
  console.log(content);
  res.send('저장 완료!');
});

app.post('/upload/image', 
    upload.single('image')
    , (req, res) => {
        console.log('req.file >>>', req.file);
        
        if (!req.file) {
            return res.status(400).json({ error: '파일이 없습니다.' });
    }
    // 업로드된 파일의 경로를 반환 (프론트에서 이미지 src로 사용)
    res.json({ url: `/uploads/${req.file.filename}` });
});

// 정적 파일 제공 (업로드된 이미지 접근)
//app.use('/uploads', express.static('uploads'));
app.listen(port, () => {
  console.log(`서버가 http://localhost:${port} 에서 실행 중입니다.`);
});
