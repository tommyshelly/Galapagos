window.RactiveArrayView = Ractive.extend({

  data: () -> {

    id:              undefined      # String
    itemType:        undefined      # String
    itemTypePlural:  undefined      # String
    createItem:      undefined      # (Number) => Any
    itemAddEvent:    "item-added"   # String
    itemRemoveEvent: "item-removed" # String
    items:           undefined      # Array[Any]
    itemTemplate:    undefined      # String
    itemClass:       undefined      # String
    viewClass:       undefined      # String
    showItems:       true           # Boolean

  }

  on: {
    'complete': (_) ->
      @resetPartial("item-template", @get('itemTemplate'))
      return

    'add-item': () ->
      number   = @get("items.length")
      creator  = @get("createItem")
      addEvent = @get("itemAddEvent")
      @push("items", creator(number))
      @set("showItems", true)
      @fire(addEvent)
      return

    'remove-item': (_, number) ->
      removeEvent = @get("itemRemoveEvent")
      @splice("items", number, 1)
      @fire(removeEvent, number)
      return
  }

  partials: {
    'item-template': "Unset",
    'delete-button': """<button class="ntb-button" type="button" on-click="[ 'remove-item', number ]">Delete</button>"""

  }

  template:
    """
    <fieldset id="{{ id }}" class="widget-edit-fieldset flex-column {{ viewClass }} {{# !showItems }}ntb-array-view-hidden{{/ showItems }}">
      <legend class="widget-edit-legend">

        {{ itemTypePlural }}

        <button class="ntb-button" type="button" on-click="[ 'add-item' ]">Add {{ itemType }}</button>

        <label class="ntb-toggle-block">
          <input id="{{ id }}-show-items" type="checkbox" checked="{{ showItems }}" />
          {{# showItems }}▲{{else}}▼{{/}}
        </label>

      </legend>
      {{# showItems }}
        {{# items:number }}
          <div class="{{ itemClass }}">
            {{> item-template }}
          </div>
        {{/ items }}
      {{/ showItems }}
    </fieldset>
    """

})
