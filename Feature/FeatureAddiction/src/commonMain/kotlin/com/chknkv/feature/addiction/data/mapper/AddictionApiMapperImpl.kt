package com.chknkv.feature.addiction.data.mapper

import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.feature.addiction.models.data.AddictionAllGroupResponse
import com.chknkv.feature.addiction.models.data.AddictionCreateRequest
import com.chknkv.feature.addiction.models.data.AddictionDetailResponse
import com.chknkv.feature.addiction.models.data.AddictionSelectedRequest
import com.chknkv.feature.addiction.models.data.AddictionSelectionGroupResponse
import com.chknkv.feature.addiction.models.data.AddictionUpdateRequest
import io.ktor.http.HttpMethod

/**
 * Реализация [AddictionApiMapper].
 *
 * @param apiClient DSL-клиент из CoreNetwork.
 */
internal class AddictionApiMapperImpl(
    private val apiClient: ApiClient,
) : AddictionApiMapper {

    override suspend fun getAddictionGroupsForSelection(): List<AddictionSelectionGroupResponse> =
        apiClient.request {
            endpoint = "addiction-groups"
            method = HttpMethod.Get
        }

    override suspend fun saveSelectedAddictions(request: AddictionSelectedRequest) {
        apiClient.request<Unit> {
            endpoint = "user/addictions/select"
            method = HttpMethod.Post
            body = request
        }
    }

    override suspend fun getAllClientAddictions(): List<AddictionAllGroupResponse> =
        apiClient.request {
            endpoint = "user/addictions/grouped"
            method = HttpMethod.Get
        }

    override suspend fun createNewClientAddiction(request: AddictionCreateRequest) {
        apiClient.request<Unit> {
            endpoint = "user/addictions"
            method = HttpMethod.Post
            body = request
        }
    }

    override suspend fun getClientDetailsAddiction(id: Int): AddictionDetailResponse =
        apiClient.request {
            endpoint = "user/addictions/$id"
            method = HttpMethod.Get
        }

    override suspend fun incrementAddictionControlDays(id: Int) {
        apiClient.request<Unit> {
            endpoint = "user/addictions/$id/increment"
            method = HttpMethod.Post
        }
    }

    override suspend fun updateClientAddiction(id: Int, request: AddictionUpdateRequest) {
        apiClient.request<Unit> {
            endpoint = "user/addictions/$id"
            method = HttpMethod.Patch
            body = request
        }
    }

    override suspend fun deleteClientAddiction(id: Int) {
        apiClient.request<Unit> {
            endpoint = "user/addictions/$id"
            method = HttpMethod.Delete
        }
    }

    companion object {

    }
}
