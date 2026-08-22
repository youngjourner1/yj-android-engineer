#include "SharedMemoryConsumer.h"
#include <cstring>

static const wchar_t* MAP_NAME = L"Global\\YJWebcamSharedFrameBuffer";
static const wchar_t* EVENT_NAME = L"Global\\YJWebcamFrameReadyEvent";

SharedMemoryConsumer::SharedMemoryConsumer()
    : m_hMapFile(NULL), m_hFrameEvent(NULL), m_pBuffer(nullptr), m_initialized(false) {
}

SharedMemoryConsumer::~SharedMemoryConsumer() {
    Close();
}

bool SharedMemoryConsumer::Initialize() {
    if (m_initialized) return true;

    m_hMapFile = OpenFileMappingW(FILE_MAP_READ, FALSE, MAP_NAME);
    if (!m_hMapFile) return false;

    m_pBuffer = MapViewOfFile(m_hMapFile, FILE_MAP_READ, 0, 0, 0);
    if (!m_pBuffer) {
        CloseHandle(m_hMapFile);
        m_hMapFile = NULL;
        return false;
    }

    m_hFrameEvent = OpenEventW(SYNCHRONIZE, FALSE, EVENT_NAME);
    m_initialized = true;
    return true;
}

bool SharedMemoryConsumer::ReadNextFrame(uint8_t* outBuffer, uint32_t maxBufferSize, uint32_t& outWidth, uint32_t& outHeight, uint64_t& outTimestampUs) {
    if (!m_initialized || !m_pBuffer) return false;

    if (m_hFrameEvent) {
        WaitForSingleObject(m_hFrameEvent, 33); // 30 FPS timeout window (~33ms)
    }

    uint8_t* pRaw = static_cast<uint8_t*>(m_pBuffer);
    outWidth = *reinterpret_cast<uint32_t*>(pRaw + 0);
    outHeight = *reinterpret_cast<uint32_t*>(pRaw + 4);
    outTimestampUs = *reinterpret_cast<uint64_t*>(pRaw + 8);
    uint32_t payloadSize = *reinterpret_cast<uint32_t*>(pRaw + 16);

    if (payloadSize > maxBufferSize) return false;

    std::memcpy(outBuffer, pRaw + 32, payloadSize);
    return true;
}

void SharedMemoryConsumer::Close() {
    if (m_pBuffer) {
        UnmapViewOfFile(m_pBuffer);
        m_pBuffer = nullptr;
    }
    if (m_hMapFile) {
        CloseHandle(m_hMapFile);
        m_hMapFile = NULL;
    }
    if (m_hFrameEvent) {
        CloseHandle(m_hFrameEvent);
        m_hFrameEvent = NULL;
    }
    m_initialized = false;
}
