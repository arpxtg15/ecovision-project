from ultralytics import YOLO

# Load a pretrained YOLOv8 model (small/fast version, good for testing)
# The first time you run this, it will auto-download the model weights (~6MB)
model = YOLO("yolov8n.pt")

# Run detection on your test image
results = model("test.png")

# Print out what it found
for result in results:
    for box in result.boxes:
        class_id = int(box.cls[0])          # numeric class ID
        class_name = model.names[class_id]   # human-readable label, e.g. "bottle"
        confidence = float(box.conf[0])      # how sure the model is (0 to 1)
        print(f"Detected: {class_name} (confidence: {confidence:.2f})")

# Optional: save an image with boxes drawn around detected objects
results[0].save(filename="result.png")
print("Saved annotated image as result.png")