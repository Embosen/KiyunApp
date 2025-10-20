# KiyunApp - 凯云科技车辆仪表盘应用

## 项目简介

KiyunApp是一个基于Android平台的车辆仪表盘监控应用，由凯云科技开发。该应用通过串口通信实时监控车辆的各项参数，包括转速、车速、油压、油温等关键指标，并提供直观的仪表盘界面显示。

## 主要功能

### 🚗 车辆监控功能
- **转速监控**: 实时显示发动机转速（0-5000 r/min）
- **车速监控**: 实时显示车辆速度（0-80 km/h）
- **油压监控**: 显示变速器油压（0-4 MPa）
- **燃油液位**: 显示燃油液位百分比（0-100%）
- **油温监控**: 显示变矩器油温（0-150℃）
- **行驶距离**: 累计行驶距离统计
- **燃油消耗**: 实时燃油消耗量统计

### 💡 灯光状态监控
- 顶灯、左转向灯、右转向灯
- 开关照明灯、示宽灯
- 远光灯、近光灯
- 制动灯、防空制动灯、防空照明灯

### 🔧 开关控制
- IGN点火开关
- 左右转向控制
- 警报灯、示宽灯控制
- 远近光灯控制
- 制动控制、防空控制

### 📊 数据可视化
- 自定义仪表盘视图（转速表、速度表）
- 柱状图显示（油压、燃油液位、油温）
- 实时数据更新
- 数字字体显示

## 技术架构

### 开发环境
- **Android SDK**: API Level 21-34
- **Java版本**: Java 17
- **Gradle版本**: 8.13
- **Android Gradle Plugin**: 8.12.2
- **构建工具**: Android Build Tools 34.0.0

### 核心技术
- **串口通信**: 基于JNI的串口通信库
- **数据解析**: 自定义协议解析（AA 55 开头，FB标志）
- **图表库**: XCL-Charts图表库
- **USB通信**: USB串口设备支持
- **服务架构**: 后台数据服务 + UI界面分离

### 项目结构
```
KiyunApp/
├── app/                    # 主应用模块
│   ├── src/main/java/     # Java源码
│   ├── src/main/jni/      # JNI串口通信代码
│   ├── src/main/res/      # 资源文件
│   └── build.gradle       # 应用构建配置
├── usbSerialForAndroid/   # USB串口通信库
├── xCLCharts/            # 图表库
├── xCLChartsdemo/        # 图表库示例
└── usbSerialExamples/    # USB串口示例
```

## 编译环境要求

### 系统要求
- **操作系统**: Windows 10/11, macOS, Linux
- **内存**: 至少8GB RAM
- **存储空间**: 至少10GB可用空间

### 开发工具
- **Android Studio**: 最新稳定版本
- **JDK**: OpenJDK 17 或 Oracle JDK 17
- **Android SDK**: API Level 21-34
- **NDK**: Android NDK（用于JNI编译）

### 依赖库
- **AndroidX**: androidx.appcompat, androidx.material
- **RecyclerView**: androidx.recyclerview
- **XCL-Charts**: 自定义图表库
- **USB Serial**: USB串口通信库

## 安装与编译

### 1. 环境准备
```bash
# 确保已安装Android Studio和JDK 17
# 配置Android SDK和NDK路径
```

### 2. 克隆项目
```bash
git clone <repository-url>
cd KiyunApp
```

### 3. 编译项目
```bash
# 使用Gradle Wrapper编译
./gradlew assembleDebug

# 或者使用Android Studio直接编译
# 打开Android Studio -> Open Project -> 选择KiyunApp目录
```

### 4. 安装APK
```bash
# 安装到连接的Android设备
./gradlew installDebug
```

## 使用说明

### 设备连接
1. 确保Android设备支持USB Host功能
2. 连接串口设备到Android设备
3. 应用会自动检测并连接串口设备（/dev/ttyS3/，波特率9600）

### 界面操作
- **主界面**: 显示所有车辆参数和状态
- **仪表盘**: 左侧显示转速，右侧显示车速
- **状态灯**: 底部显示各种灯光状态
- **控制开关**: 底部显示各种控制开关状态
- **隐藏功能**: 连续点击速度表3次可进入原始数据界面

### 数据协议
应用使用自定义串口通信协议：
- **帧头**: AA 55
- **长度**: 数据长度字节
- **标志**: FB
- **数据**: 具体参数数据
- **帧尾**: AA 55

## 权限说明

应用需要以下权限：
- `android.permission.RECEIVE_BOOT_COMPLETED`: 开机自启动
- `android.permission.MOUNT_UNMOUNT_FILESYSTEMS`: SD卡文件操作
- `android.permission.WRITE_EXTERNAL_STORAGE`: 外部存储写入
- `android.hardware.usb.host`: USB Host功能

## 故障排除

### 常见问题
1. **串口连接失败**: 检查设备权限和串口路径
2. **数据不显示**: 检查串口通信协议和数据格式
3. **应用崩溃**: 查看日志文件（存储在/sdcard/kiyun_crash/）

### 日志查看
```bash
# 使用adb查看日志
adb logcat | grep "cchen"
```

## 开发团队

- **开发公司**: 凯云科技
- **项目类型**: 车辆监控系统
- **技术栈**: Android, JNI, 串口通信, 数据可视化

## 许可证

本项目采用Apache License 2.0许可证，详情请查看LICENSE文件。

## 联系方式

如有问题或建议，请联系开发团队。

---

**注意**: 本应用专为特定车辆监控系统设计，使用时请确保硬件设备兼容性。
