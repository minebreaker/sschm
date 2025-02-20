import { UseRequestState, useRequestWith } from "../lib/net"
import { ChangeEvent, useCallback, useEffect } from "react"


type PhotoUploadResponse = {
  id: string
}

const fetcher = (url: string, request: any) => {
  return fetch(url, {
    body: request,
    method: "POST"
    //headers: { "Content-Type": "multipart/form-data" }
  })
}

export function UploadPhoto(props: { onUpload: (id: string) => void }) {

  const uploadPhoto = useRequestWith<PhotoUploadResponse, any>("/api/photo", fetcher)

  const onChangeFile = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files && e.target.files.length !== 0 && e.target.files.item(0)
    if (!file) {
      return
    }

    const multipart = new FormData()
    multipart.set("file", file)
    uploadPhoto.submit(multipart)

  }, [uploadPhoto.submit])

  useEffect(() => {
    if (uploadPhoto.type === UseRequestState.Completed) {
      props.onUpload(uploadPhoto.data.id)
    }
  }, [uploadPhoto.type, props.onUpload]);

  return (
    <div>
      <input type="file" placeholder="Select file" accept="image/*" onChange={onChangeFile} />

      {uploadPhoto.type === UseRequestState.Loading && (
        <p>Uploading...</p>
      )}
      {uploadPhoto.type === UseRequestState.Error && (
        <p>Failed to upload photo.</p>
      )}
      {uploadPhoto.type === UseRequestState.Completed && (
        <p>Uploaded.</p>
      )}
    </div>
  )
}
