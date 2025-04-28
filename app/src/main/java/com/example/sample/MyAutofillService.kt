package com.example.sample

import android.app.assist.AssistStructure

import android.service.autofill.AutofillService
import android.service.autofill.FillCallback

import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.Dataset
import android.view.View
import android.view.autofill.AutofillValue
import android.widget.RemoteViews


class MyAutofillService : AutofillService() {

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: android.os.CancellationSignal,
        callback: FillCallback
    ) {
        val username = "yourUsername" // Replace with saved username
        val password = "yourPassword" // Replace with saved password

        val context = request.fillContexts.lastOrNull()
        val structure = context?.structure ?: run {
            callback.onFailure("No structure available")
            return
        }

        val nodes = structure.findViewNodes()

        val datasetBuilder = Dataset.Builder()

        for (node in nodes) {
            val hints = node.autofillHints

            if (hints?.any { it == View.AUTOFILL_HINT_USERNAME } == true) {
                node.autofillId?.let { autofillId ->
                    datasetBuilder.setValue(
                        autofillId,
                        AutofillValue.forText(username),
                        createPresentation("Username")
                    )
                }
            } else if (hints?.any { it == View.AUTOFILL_HINT_PASSWORD } == true) {
                node.autofillId?.let { autofillId ->
                    datasetBuilder.setValue(
                        autofillId,
                        AutofillValue.forText(password),
                        createPresentation("Password")
                    )
                }
            }
        }

        val dataset = datasetBuilder.build()

        val response = FillResponse.Builder()
            .addDataset(dataset)
            .build()

        callback.onSuccess(response)
    }

    override fun onSaveRequest(
        request: android.service.autofill.SaveRequest,
        callback: android.service.autofill.SaveCallback
    ) {
        // Handle saving new data if needed
        callback.onSuccess()
    }

    private fun createPresentation(text: String): RemoteViews {
        val presentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
        presentation.setTextViewText(android.R.id.text1, text)
        return presentation
    }
}

/** Helper: Traverse all ViewNodes inside AssistStructure **/
private fun AssistStructure.findViewNodes(): List<AssistStructure.ViewNode> {
    val nodes = mutableListOf<AssistStructure.ViewNode>()
    for (i in 0 until windowNodeCount) {
        val windowNode = getWindowNodeAt(i)
        traverseViewNode(windowNode.rootViewNode, nodes)
    }
    return nodes
}

private fun traverseViewNode(
    node: AssistStructure.ViewNode,
    list: MutableList<AssistStructure.ViewNode>
) {
    list.add(node)
    for (i in 0 until node.childCount) {
        node.getChildAt(i)?.let { child ->
            traverseViewNode(child, list)
        }
    }
}
