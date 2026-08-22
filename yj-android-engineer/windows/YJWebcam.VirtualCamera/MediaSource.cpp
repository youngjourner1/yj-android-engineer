#include "MediaSource.h"

YJMediaSource::YJMediaSource()
    : m_refCount(1), m_isShutdown(false) {
    MFCreateEventQueue(&m_pEventQueue);
    m_consumer.Initialize();
}

YJMediaSource::~YJMediaSource() {
    Shutdown();
}

STDMETHODIMP YJMediaSource::QueryInterface(REFIID riid, void** ppv) {
    if (!ppv) return E_POINTER;
    if (riid == IID_IUnknown || riid == IID_IMFMediaEventGenerator || riid == IID_IMFMediaSource) {
        *ppv = static_cast<IMFMediaSource*>(this);
        AddRef();
        return S_OK;
    }
    *ppv = nullptr;
    return E_NOINTERFACE;
}

STDMETHODIMP_(ULONG) YJMediaSource::AddRef() {
    return InterlockedIncrement(&m_refCount);
}

STDMETHODIMP_(ULONG) YJMediaSource::Release() {
    ULONG count = InterlockedDecrement(&m_refCount);
    if (count == 0) {
        delete this;
    }
    return count;
}

STDMETHODIMP YJMediaSource::GetEvent(DWORD dwFlags, IMFMediaEvent** ppEvent) {
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return m_pEventQueue->GetEvent(dwFlags, ppEvent);
}

STDMETHODIMP YJMediaSource::BeginGetEvent(IMFAsyncCallback* pCallback, IUnknown* punkState) {
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return m_pEventQueue->BeginGetEvent(pCallback, punkState);
}

STDMETHODIMP YJMediaSource::EndGetEvent(IMFAsyncResult* pResult, IMFMediaEvent** ppEvent) {
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return m_pEventQueue->EndGetEvent(pResult, ppEvent);
}

STDMETHODIMP YJMediaSource::QueueEvent(MediaEventType met, REFGUID guidExtendedType, HRESULT hrStatus, const PROPVARIANT* pvValue) {
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return m_pEventQueue->QueueEventParamVar(met, guidExtendedType, hrStatus, pvValue);
}

STDMETHODIMP YJMediaSource::GetCharacteristics(DWORD* pdwCharacteristics) {
    if (!pdwCharacteristics) return E_POINTER;
    if (m_isShutdown) return MF_E_SHUTDOWN;
    *pdwCharacteristics = MFMEDIASOURCE_IS_LIVE;
    return S_OK;
}

STDMETHODIMP YJMediaSource::CreatePresentationDescriptor(IMFPresentationDescriptor** ppPresentationDescriptor) {
    if (!ppPresentationDescriptor) return E_POINTER;
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return S_OK;
}

STDMETHODIMP YJMediaSource::Start(IMFPresentationDescriptor* pPresentationDescriptor, const GUID* pguidTimeFormat, const PROPVARIANT* pvarStartPosition) {
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return QueueEvent(MESourceStarted, GUID_NULL, S_OK, pvarStartPosition);
}

STDMETHODIMP YJMediaSource::Stop() {
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return QueueEvent(MESourceStopped, GUID_NULL, S_OK, nullptr);
}

STDMETHODIMP YJMediaSource::Pause() {
    if (m_isShutdown) return MF_E_SHUTDOWN;
    return QueueEvent(MESourcePaused, GUID_NULL, S_OK, nullptr);
}

STDMETHODIMP YJMediaSource::Shutdown() {
    if (m_isShutdown) return S_OK;
    m_isShutdown = true;
    if (m_pEventQueue) {
        m_pEventQueue->Shutdown();
        m_pEventQueue.Reset();
    }
    m_consumer.Close();
    return S_OK;
}
