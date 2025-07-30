// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.primereact.SelectItem

import scalajs.js

final case class FormEnumDropdownOptionalView[V[_], A](
  id:                   NonEmptyString,
  value:                V[Option[A]],
  label:                js.UndefOr[TagMod] = js.undefined,
  exclude:              Set[A] = Set.empty[A],
  disabledItems:        Set[A] = Set.empty[A],
  clazz:                js.UndefOr[Css] = js.undefined,
  panelClass:           js.UndefOr[Css] = js.undefined,
  labelClass:           js.UndefOr[Css] = js.undefined,
  showClear:            Boolean =
    true,         // The default in `Dropdown` is false, but in this case we usually want true.
  filter:               js.UndefOr[Boolean] = js.undefined,
  showFilterClear:      js.UndefOr[Boolean] = js.undefined,
  disabled:             js.UndefOr[Boolean] = js.undefined,
  placeholder:          js.UndefOr[String] = js.undefined,
  size:                 js.UndefOr[PlSize] = js.undefined,
  emptyMessage:         js.UndefOr[VdomNode] = js.undefined,
  itemTemplate:         js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  valueTemplate:        js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  emptyMessageTemplate: js.UndefOr[VdomNode] = js.undefined,
  onChangeE:            js.UndefOr[(Option[A], ReactEvent) => Callback] =
    js.undefined, // called after the view is set
  modifiers:            Seq[TagMod] = Seq.empty
)(using
  val enumerated:       Enumerated[A],
  val display:          Display[A],
  val vl:               ViewLike[V]
) extends ReactFnProps(FormEnumDropdownOptionalView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object FormEnumDropdownOptionalView:
  private type AnyF[_] = Any

  private def buildComponent[V[_], A] = ScalaFnComponent[FormEnumDropdownOptionalView[V, A]]:
    props =>
      import props.given

      React.Fragment(
        props.label.map(l =>
          FormLabel(htmlFor = props.id, size = props.size, clazz = props.labelClass)(l)
        ),
        EnumDropdownOptionalView(
          id = props.id,
          value = props.value,
          exclude = props.exclude,
          disabledItems = props.disabledItems,
          clazz = LucumaPrimeStyles.FormField |+| props.clazz.toOption.orEmpty,
          panelClass = props.panelClass,
          showClear = props.showClear,
          filter = props.filter,
          showFilterClear = props.showFilterClear,
          disabled = props.disabled,
          placeholder = props.placeholder,
          size = props.size,
          emptyMessage = props.emptyMessage,
          itemTemplate = props.itemTemplate,
          valueTemplate = props.valueTemplate,
          emptyMessageTemplate = props.emptyMessageTemplate,
          onChangeE = props.onChangeE,
          modifiers = props.modifiers
        )
      )

  private val component = buildComponent[AnyF, Any]
