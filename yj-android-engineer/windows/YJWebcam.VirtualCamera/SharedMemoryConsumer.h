#pragma once
#include <windows.h>
#include <cstdint>

class SharedMemoryConsumer {
public:
    SharedMemoryConsumer();
    ~SharedMemoryConsumer();

    bool Initialize();
    bool ReadNextFrame(uint8_t* outBuffer, uint32_t maxBufferSize, uint32_t& outWidth, uint32_t& outHeight, uint64_t& outTimestampUs);
    void Close();

private:
    HANDLE m_hMapFile;
    HANDLE m_hFrameEvent;
    void* m_pBuffer;
    bool m_initialized;
};
